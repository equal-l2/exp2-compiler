package lang.c.parse.primary;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.parse.expression.Expression;

import java.io.PrintStream;

public class UnsignedFactor extends CParseRule {
	// unsignedFactor ::= factorAmp | number | LPAR expression RPAR | primary
	private CParseRule factor;

	public static boolean isFirst(CToken tk) {
		return FactorAmp.isFirst(tk) || Number.isFirst(tk) || tk.getType() == CToken.TK_LPAR || Primary.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer tknz = pcx.getTokenizer();
		CToken tk = tknz.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LPAR) {
			tk = tknz.getNextToken(pcx); // '(' を読み飛ばす
			if (!Expression.isFirst(tk)) {
				pcx.fatalError(tk.toExplainString() + "expected expression");
			}
			factor = new Expression();
			factor.parse(pcx);

			tk = tknz.getCurrentToken(pcx);
			if (tk.getType() != CToken.TK_RPAR) {
				pcx.fatalError(tk.toExplainString() + "expected ')'");
			}
			tknz.getNextToken(pcx);
		} else {
			if (FactorAmp.isFirst(tk)) {
				factor = new FactorAmp();
			} else if (Number.isFirst(tk)) {
				factor = new Number();
			} else if (Primary.isFirst(tk)) {
				factor = new Primary();
			} else {
				pcx.fatalError(tk.toExplainString() + " expected (factorAmp | number | LPAR expression RPAR | primary)");
			}
			factor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		factor.semanticCheck(pcx);
		setCType(factor.getCType());
		setConstant(factor.isConstant());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; unsignedFactor starts");
		factor.codeGen(pcx);
		o.println(";;; unsignedFactor completes");
	}
}