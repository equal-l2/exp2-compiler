package lang.c.parse.statement;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class Statement extends CParseRule {
	// statement ::= statementAssign
	private CParseRule stmt;

	public static boolean isFirst(CToken tk) {
		return StatementAssign.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		stmt = new StatementAssign();
		stmt.parse(pcx);
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		stmt.semanticCheck(pcx);
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		stmt.codeGen(pcx);
	}
}
