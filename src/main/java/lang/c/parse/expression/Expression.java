package lang.c.parse.expression;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.term.Term;

import java.io.PrintStream;

public class Expression extends CParseRule {
	// expression ::= term { expressionAdd | expressionSub }
	private CParseRule expression;

	public static boolean isFirst(CToken tk) {
		return Term.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule term = new Term();
		term.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		CParseRule list;
		while (true) {
			if (ExpressionAdd.isFirst(tk)) {
				list = new ExpressionAdd(term);
			} else if (ExpressionSub.isFirst(tk)) {
				list = new ExpressionSub(term);
			} else {
				break;
			}
			list.parse(pcx);
			term = list;
			tk = ct.getCurrentToken(pcx);
		}
		expression = term;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		expression.semanticCheck(pcx);
		setCType(expression.getCType());
		setConstant(expression.isConstant());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; expression starts");
		expression.codeGen(pcx);
		o.println(";;; expression completes");
	}
}

