package lang.c.parse.stmt;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.expr.Expression;
import lang.c.parse.prim.Primary;

import java.io.PrintStream;

public class StatementAssign extends CParseRule {
	// statementAssign ::= primary ASSIGN expression SEMI

	private Primary prim;
	private CToken eq;
	private Expression expr;

	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		prim = new Primary();
		prim.parse(pctx);

		eq = pctx.consume(CToken.TK_ASSIGN, "expected '='");

		expr = new Expression();
		expr.parse(pctx);

		pctx.consume(CToken.TK_SEMI, "expected ';'");
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		prim.semanticCheck(pctx);
		expr.semanticCheck(pctx);
		if (!prim.getCType().isCType(expr.getCType())) {
			pctx.fatalError(eq.toExplainString() + "cannot assign " + expr.getCType() + " to " + prim.getCType());
		}
		if (prim.isConstant()) {
			pctx.fatalError("lhs is const");
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; statementAssign starts");
		expr.codeGen(pctx);
		prim.codeGen(pctx);
		o.println("\tMOV\t-(R6), R1\t; 左辺のアドレスをポップ");
		o.println("\tMOV\t-(R6), R0\t; 右辺の値をポップ"); // TODO: chained assignment?
		o.println("\tMOV\tR0, (R1)\t; 値を指定アドレスへ");
		o.println(";;; statementAssign completes");
	}
}
