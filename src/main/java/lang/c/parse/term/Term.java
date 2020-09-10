package lang.c.parse.term;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.parse.factor.Factor;

import java.io.PrintStream;

public class Term extends CParseRule {
	// term ::= factor { termMult | termDiv }
	private CParseRule term;

	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule factor = new Factor();
		factor.parse(pcx);

		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		CParseRule list;
		while (true) {
			if (TermMult.isFirst(tk)) {
				list = new TermMult(factor);
			} else if (TermDiv.isFirst(tk)) {
				list = new TermDiv(factor);
			} else {
				break;
			}
			list.parse(pcx);
			factor = list;
			tk = ct.getCurrentToken(pcx);
		}
		term = factor;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		term.semanticCheck(pcx);
		setCType(term.getCType());
		setConstant(term.isConstant());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		term.codeGen(pcx);
		o.println(";;; term completes");
	}
}

