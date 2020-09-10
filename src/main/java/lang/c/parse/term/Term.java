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

	public void parse(CParseContext pctx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule factor = new Factor();
		factor.parse(pctx);

		CTokenizer ct = pctx.getTokenizer();
		CToken tk = ct.getCurrentToken(pctx);
		CParseRule list;
		while (true) {
			if (TermMult.isFirst(tk)) {
				list = new TermMult(factor);
			} else if (TermDiv.isFirst(tk)) {
				list = new TermDiv(factor);
			} else {
				break;
			}
			list.parse(pctx);
			factor = list;
			tk = ct.getCurrentToken(pctx);
		}
		term = factor;
	}

	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		term.semanticCheck(pctx);
		setCType(term.getCType());
		setConstant(term.isConstant());
	}

	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; term starts");
		term.codeGen(pctx);
		o.println(";;; term completes");
	}
}

