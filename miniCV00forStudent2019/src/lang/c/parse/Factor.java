package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// factor ::= factorAmp | number
	private CParseRule factor;
	public Factor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return FactorAmp.isFirst(tk) || Number.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		System.err.println("FACTOR");
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (FactorAmp.isFirst(tk)) {
			factor = new FactorAmp(pcx);
		} else if (Number.isFirst(tk)) {
			factor = new Number(pcx);
		}
		factor.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType());		// number の型をそのままコピー
			setConstant(factor.isConstant());	// number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (factor != null) { factor.codeGen(pcx); }
		o.println(";;; factor completes");
	}
}