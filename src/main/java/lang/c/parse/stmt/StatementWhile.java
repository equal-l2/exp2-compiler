package lang.c.parse.stmt;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.cond.Condition;

import java.util.ArrayList;

public class StatementWhile extends CParseRule {
	// statementWhile ::= WHILE LPAR condition RPAR stmtBlock

	private Condition cond;
	private StmtBlock body;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_WHILE;
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
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		cond.semanticCheck(pctx);
		body.semanticCheck(pctx);
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		var o = pctx.getIOContext().getOutStream();
		int seq = pctx.getSeqId();

		o.println(";;; statementWhile starts");

		o.println("BEGIN" + seq + ":");
		cond.codeGen(pctx);
		o.println("\tMOV\t-(R6), R0\t; pop cond result and set Z");

		o.println("\tBRZ\t" + "END" + seq + "\t; ");

		body.codeGen(pctx);
		o.println("\tJMP\t" + "BEGIN" + seq + "\t; ");

		o.println("END" + seq + ":");
		o.println(";;; statementWhile completes");
	}
}
