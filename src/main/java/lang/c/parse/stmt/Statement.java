package lang.c.parse.stmt;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class Statement extends CParseRule {
	/*
	statement ::= statementAssign
                | statementIf
                | statementWhile
                | statementInput
                | statementOutput
	*/
	private CParseRule stmt;

	public static boolean isFirst(CToken tk) {
		return StatementAssign.isFirst(tk)
				|| StatementIf.isFirst(tk)
				|| StatementWhile.isFirst(tk)
				|| StatementInput.isFirst(tk)
				|| StatementOutput.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CToken tk = pctx.getTokenizer().getCurrentToken(pctx);

		if (StatementAssign.isFirst(tk)) {
			stmt = new StatementAssign();
		} else if (StatementIf.isFirst(tk)) {
			stmt = new StatementIf();
		} else if (StatementWhile.isFirst(tk)) {
			stmt = new StatementWhile();
		} else if (StatementInput.isFirst(tk)) {
			stmt = new StatementInput();
		} else if (StatementOutput.isFirst(tk)) {
			stmt = new StatementOutput();
		} else {
			pctx.fatalError(tk.toExplainString() + " unexpected token for statement");
		}
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
