package lang.c.parse.variable;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.expression.Expression;

import java.io.PrintStream;

public class Array extends CParseRule {
	// array ::= LBRA expression RBRA
	private CParseRule expr;
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LBRA;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer tknz = pcx.getTokenizer();
		CToken tk = tknz.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			expr = new Expression();
		} else {
			pcx.fatalError(tk.toExplainString() + "expected expression");
		}
		expr.parse(pcx);

		tk = tknz.getCurrentToken(pcx);
		if (tk.getType() != CToken.TK_RBRA) {
			pcx.fatalError(tk.toExplainString() + "expected ']'");
		}
		tknz.getNextToken(pcx);
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		expr.semanticCheck(pcx);
		CType ty = expr.getCType();
		if (!ty.isCType(CType.T_int)) {
			pcx.fatalError("cannot index array with " + ty);
		}
		// setCType and setConstant ain't required as this is not a value
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; array starts");
		expr.codeGen(pcx);
		o.println(";;; array completes");
	}
}
