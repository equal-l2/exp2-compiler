package lang.c.parse.factor;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.primary.UnsignedFactor;

import java.io.PrintStream;

public class MinusFactor extends CParseRule {
	// minusFactor ::= MINUS unsignedFactor
	private CToken op;
	private CParseRule uFactor;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx); // op を読み飛ばす
		if (UnsignedFactor.isFirst(tk)) {
			uFactor = new UnsignedFactor();
		} else {
			pcx.fatalError(tk.toExplainString() + "expected unsignedFactor");
		}
		uFactor.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 単項マイナスの型規則
		final int[] rule = {
				CType.T_err, // T_err
				CType.T_int, // T_int
				CType.T_err, // T_pint
		};

		uFactor.semanticCheck(pcx);
		int t = uFactor.getCType().getType();
		if (rule[t] == CType.T_err) {
			pcx.fatalError(op.toExplainString() + "型[" + uFactor.getCType() + "]に単項マイナス演算子は適用できません");
		}
		setCType(CType.getCType(t));
		setConstant(uFactor.isConstant());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; minusFactor starts");
		uFactor.codeGen(pcx); // 式部分のコードを生成

		/* -x を得るために 0-x を行う */
		o.println("\tMOV\t-(R6), R1;");
		o.println("\tMOV\t#0, R0;");
		o.println("\tSUB\tR1, R0");
		o.println("\tMOV\tR0, (R6)+");
		o.println(";;; minusFactor completes");
	}
}
