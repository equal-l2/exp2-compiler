package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

import java.io.PrintStream;

public class UnsignedFactor extends CParseRule {
	// unsignedFactor ::= factorAmp | number | LPAR expression RPAR
	private CParseRule factor;

	public UnsignedFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return FactorAmp.isFirst(tk) || Number.isFirst(tk) || tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
			factor = new Expression(pcx);
			factor.parse(pcx);
			ct.getNextToken(pcx); // ')'を読み飛ばす
		} else {
			if (FactorAmp.isFirst(tk)) {
				factor = new FactorAmp(pcx);
			} else if (Number.isFirst(tk)) {
				factor = new Number(pcx);
			}
			factor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType());        // number の型をそのままコピー
			setConstant(factor.isConstant());    // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; unsignedFactor starts");
		if (factor != null) {
			factor.codeGen(pcx);
		}
		o.println(";;; unsignedFactor completes");
	}
}