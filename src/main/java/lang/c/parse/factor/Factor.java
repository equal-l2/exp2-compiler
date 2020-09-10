package lang.c.parse.factor;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.UnsignedFactor;

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
		} else {
			pcx.fatalError(tk.toExplainString() + "expected plusFactor | minusFactor | unsignedFactor");
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

