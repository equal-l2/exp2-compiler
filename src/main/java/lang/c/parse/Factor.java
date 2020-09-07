package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class Factor extends CParseRule {
	// factor ::= plusFactor | minusFactor | unsignedFactor
	private CParseRule factor;

	public static boolean isFirst(CToken tk) {
		return PlusFactor.isFirst(tk) || MinusFactor.isFirst(tk) || UnsignedFactor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (PlusFactor.isFirst(tk)) {
			factor = new PlusFactor();
		} else if (MinusFactor.isFirst(tk)) {
			factor = new MinusFactor();
		} else if (UnsignedFactor.isFirst(tk)) {
			factor = new UnsignedFactor();
		}
		factor.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		factor.semanticCheck(pcx);
		setCType(factor.getCType());
		setConstant(factor.isConstant());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		factor.codeGen(pcx);
		o.println(";;; factor completes");
	}
}

class PlusFactor extends CParseRule {
	// plusFactor ::= PLUS unsignedFactor
	private CParseRule unsignedFactor;

	public static boolean isFirst(CToken tk) { return tk.getType() == CToken.TK_PLUS; }
	public void parse (CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		ct.getNextToken(pcx); // '+' を読み飛ばす
		unsignedFactor = new UnsignedFactor();
		unsignedFactor.parse(pcx);
	}

	public void semanticCheck (CParseContext pcx) throws FatalErrorException {
		if (unsignedFactor != null) {
			unsignedFactor.semanticCheck(pcx);
			setCType(unsignedFactor.getCType());
			setConstant(unsignedFactor.isConstant());
		}
	}

	public void codeGen (CParseContext pcx) throws FatalErrorException {
		if (unsignedFactor != null) {
			// unsignedFactorの値は中身のunsignedFactorと同じ
			unsignedFactor.codeGen(pcx);
		}
	}
}

class MinusFactor extends CParseRule {
	// minusFactor ::= MINUS unsignedFactor
	private CToken op;
	private CParseRule uFactor;

	public static boolean isFirst(CToken tk) { return tk.getType() == CToken.TK_MINUS; }
	public void parse (CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		ct.getNextToken(pcx); // op を読み飛ばす
		uFactor = new UnsignedFactor();
		uFactor.parse(pcx);
	}

	public void semanticCheck (CParseContext pcx) throws FatalErrorException {
		if (uFactor != null) {
			// 単項マイナスの型規則
			final int[] s = {
					CType.T_err, // T_err
					CType.T_int, // T_int
					CType.T_err, // T_pint
			};

			uFactor.semanticCheck(pcx);
			int t = uFactor.getCType().getType();
			if (s[t] == CType.T_err) {
				pcx.fatalError(op.toExplainString() + "型[" + uFactor.getCType().toString() + "]に単項マイナス演算子は適用できません");
			}
			setCType(CType.getCType(t));
			setConstant(uFactor.isConstant());
		}
	}

	public void codeGen (CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; minusFactor starts");
		if (uFactor != null) {
			uFactor.codeGen(pcx); // 式部分のコードを生成

			/* -x を得るために 0-x を行う */
			o.println("\tMOV\t-(R6), R1;");
			o.println("\tMOV\t#0, R0;");
			o.println("\tSUB\tR1, R0");
			o.println("\tMOV\tR0, (R6)+");
		}
		o.println(";;; minusFactor completes");
	}
}
