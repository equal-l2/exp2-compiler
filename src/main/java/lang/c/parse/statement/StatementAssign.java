package lang.c.parse.statement;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.parse.expression.Expression;
import lang.c.parse.primary.Primary;

import java.io.PrintStream;

public class StatementAssign extends CParseRule {
	// statementAssign ::= primary ASSIGN expression SEMI

	private CParseRule prim;
	private CParseRule expr;

	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer tknz = pcx.getTokenizer();

		prim = new Primary();
		prim.parse(pcx);

		CToken tk = tknz.getCurrentToken(pcx);
		if (tk.getType() != CToken.TK_ASSIGN) {
			pcx.fatalError(tk.toExplainString() + "expected '='");
		}
		tknz.getNextToken(pcx);

		expr = new Expression();
		expr.parse(pcx);

		tk = tknz.getCurrentToken(pcx);
		if (tk.getType() != CToken.TK_SEMI) {
			pcx.fatalError(tk.toExplainString() + "expected ';'");
		}
		tknz.getNextToken(pcx);
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		prim.semanticCheck(pcx);
		expr.semanticCheck(pcx);
		if (prim.isConstant()) {
			pcx.fatalError("lhs is const");
		} else if (!prim.getCType().isCType(expr.getCType())) {
			pcx.fatalError("cannot assign " + expr.getCType() + " to " + prim.getCType());
		}
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementAssign starts");
		expr.codeGen(pcx);
		prim.codeGen(pcx);
		o.println("\tMOV\t-(R6), R1\t; 左辺のアドレスをポップ");
		o.println("\tMOV\t-(R6), R0\t; 右辺の値をポップ"); // TODO: chained assignment?
		o.println("\tMOV\tR0, (R1)\t; 値を指定アドレスへ");
		o.println(";;; statementAssign completes");
	}
}
