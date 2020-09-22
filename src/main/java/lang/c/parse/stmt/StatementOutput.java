package lang.c.parse.stmt;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.expr.Expression;
import lang.c.parse.prim.Primary;

public class StatementOutput extends CParseRule {
	// statementOutput ::= OUTPUT expression SEMI

	private Expression expr;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_OUTPUT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		pctx.getTokenizer().getNextToken(pctx);

		pctx.expect(Expression::isFirst, "expected expression");
		expr = new Expression();
		expr.parse(pctx);

		pctx.consume(CToken.TK_SEMI, "expected ';'");
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		expr.semanticCheck(pctx);
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		var o = pctx.getIOContext().getOutStream();
		o.println(";;; statementOutput starts");
		expr.codeGen(pctx);
		o.println("\tMOV\t-(R6), R0\t;");
		o.println("\tMOV\t#0xFFE0, R1\t;");
		o.println("\tMOV\tR0, (R1)\t;");
		o.println(";;; statementOutput completes");
	}
}
