package lang.c.parse.stmt;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class Statement extends CParseRule {
	// statement ::= statementAssign
	private StatementAssign stmt;

	public static boolean isFirst(CToken tk) {
		return StatementAssign.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		stmt = new StatementAssign();
		stmt.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		stmt.semanticCheck(pctx);
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		stmt.codeGen(pctx);
	}
}
