package lang.c.parse.stmt;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.cond.Condition;

public class StatementIf extends CParseRule {
	// statementIf ::= IF LPAR condition RPAR stmtBlock [ ELSE stmtBlock ]

	private Condition cond;
	private StmtBlock body;
	private StmtBlock elseBody;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IF;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		var tknz = pctx.getTokenizer();
		tknz.getNextToken(pctx);

		pctx.consume(CToken.TK_LPAR, "expected '('");

		pctx.expect(Condition::isFirst, "expected condition");
		cond = new Condition();
		cond.parse(pctx);

		pctx.consume(CToken.TK_RPAR, "expected ')'");

		pctx.expect(StmtBlock::isFirst, "expected statement(s)");
		body = new StmtBlock();
		body.parse(pctx);

		var tk = tknz.getCurrentToken(pctx);
		if (tk.getType() == CToken.TK_ELSE) {
			tknz.getNextToken(pctx);
			pctx.expect(StmtBlock::isFirst, "expected statement(s)");
			elseBody = new StmtBlock();
			elseBody.parse(pctx);
		}
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		cond.semanticCheck(pctx);
		body.semanticCheck(pctx);
		if (elseBody != null) {
			elseBody.semanticCheck(pctx);
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		var o = pctx.getIOContext().getOutStream();
		boolean hasElse = elseBody != null;
		int seq = pctx.getSeqId();

		o.println(";;; statementIf starts");

		cond.codeGen(pctx);
		o.println("\tMOV\t-(R6), R0\t; pop cond result and set Z");

		o.println("\tBRZ\t" + "ELSE" + seq + "\t; ");

		body.codeGen(pctx);
		o.println("\tJMP\t" + "END" + seq + "\t; ");

		o.println("ELSE" + seq + ":");
		if (elseBody != null) {
			elseBody.codeGen(pctx);
		}

		o.println("END" + seq + ":");
		o.println(";;; statementIf completes");
	}
}
