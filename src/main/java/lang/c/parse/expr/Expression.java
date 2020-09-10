package lang.c.parse.expr;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.parse.term.Term;

import java.io.PrintStream;

public class Expression extends CParseRule {
	// expression ::= term { expressionAdd | expressionSub }
	private CParseRule expression;

	public static boolean isFirst(CToken tk) {
		return Term.isFirst(tk);
	}

	public void parse(CParseContext pctx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule term = new Term();
		term.parse(pctx);

		CTokenizer tknz = pctx.getTokenizer();
		CToken tk = tknz.getCurrentToken(pctx);
		CParseRule list;
		while (true) {
			if (ExpressionAdd.isFirst(tk)) {
				list = new ExpressionAdd(term);
			} else if (ExpressionSub.isFirst(tk)) {
				list = new ExpressionSub(term);
			} else {
				break;
			}
			list.parse(pctx);
			term = list;
			tk = tknz.getCurrentToken(pctx);
		}
		expression = term;
	}

	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		expression.semanticCheck(pctx);
		setCType(expression.getCType());
		setConstant(expression.isConstant());
	}

	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; expression starts");
		expression.codeGen(pctx);
		o.println(";;; expression completes");
	}
}

