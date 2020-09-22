package lang.c.parse.stmt;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.prim.Primary;

public class StatementInput extends CParseRule {
	// statementInput ::= INPUT primary SEMI

	private CToken op;
	private Primary prim;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INPUT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		var tknz = pctx.getTokenizer();
		op = tknz.getCurrentToken(pctx);
		tknz.getNextToken(pctx);

		pctx.expect(Primary::isFirst, "expected primary");
		prim = new Primary();
		prim.parse(pctx);

		pctx.consume(CToken.TK_SEMI, "expected ';'");
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		prim.semanticCheck(pctx);
		if (prim.isConstant()) {
			pctx.fatalError(op.toExplainString() + " cannot input to constant");
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		var o = pctx.getIOContext().getOutStream();
		o.println(";;; statementInput starts");
		prim.codeGen(pctx);
		o.println("\tMOV\t#0xFFE0, R0\t;");
		o.println("\tMOV\t-(R6), R1\t;");
		o.println("\tMOV\t(R0), (R1)\t;");
		o.println(";;; statementInput completes");
	}
}
