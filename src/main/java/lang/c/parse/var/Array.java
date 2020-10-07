package lang.c.parse.var;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.expr.Expression;

import java.io.PrintStream;

public class Array extends CParseRule {
	// array ::= LBRA expression RBRA

	private CToken op;
	private Expression expr;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LBRA;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CTokenizer tknz = pctx.getTokenizer();
		op = tknz.getCurrentToken(pctx);
		tknz.getNextToken(pctx); // '[' を読み飛ばす
		pctx.expect(Expression::isFirst, "expected expression");
		expr = new Expression();
		expr.parse(pctx);

		pctx.consume(CToken.TK_RBRA, "expected ']'");
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		expr.semanticCheck(pctx);
		CType ty = expr.getCType();
		if (!ty.isCType(CType.T_int)) {
			pctx.fatalError(
					op.toExplainString() + " expected type 'int', got '" + ty + "'",
					"array subscript must be 'int'");
		}
		// setCType and setConstant ain't required as this is not a value
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; array starts");
		expr.codeGen(pctx);
		o.println(";;; array completes");
	}
}
