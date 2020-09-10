package lang.c.parse.factor;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.prim.UnsignedFactor;

import java.io.PrintStream;

public class MinusFactor extends CParseRule {
	// minusFactor ::= MINUS unsignedFactor
	private CToken op;
	private UnsignedFactor uFactor;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}

	public void parse(CParseContext pctx) throws FatalErrorException {
		op = pctx.take();
		pctx.expect(UnsignedFactor::isFirst, "expected unsignedFactor");
		uFactor = new UnsignedFactor();
		uFactor.parse(pctx);
	}

	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		// 単項マイナスの型規則
		final int[] rule = {
				CType.T_err, // T_err
				CType.T_int, // T_int
				CType.T_err, // T_pint
		};

		uFactor.semanticCheck(pctx);
		int t = uFactor.getCType().getType();
		if (rule[t] == CType.T_err) {
			pctx.fatalError(op.toExplainString() + "型[" + uFactor.getCType() + "]に単項マイナス演算子は適用できません");
		}
		setCType(CType.getCType(t));
		setConstant(uFactor.isConstant());
	}

	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; minusFactor starts");
		uFactor.codeGen(pctx); // 式部分のコードを生成

		/* -x を得るために 0-x を行う */
		o.println("\tMOV\t-(R6), R1;");
		o.println("\tMOV\t#0, R0;");
		o.println("\tSUB\tR1, R0");
		o.println("\tMOV\tR0, (R6)+");
		o.println(";;; minusFactor completes");
	}
}
