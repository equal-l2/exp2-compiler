package lang.c.parse.stmt;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.util.ArrayList;

public class StmtBlock extends CParseRule {
	// stmtBlock ::= statement | LCUR { statement } RCUR

	private Statement single;
	private ArrayList<Statement> body = new ArrayList<>();

	public static boolean isFirst(CToken tk) {
		return Statement.isFirst(tk) || tk.getType() == CToken.TK_LCUR;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		var tknz = pctx.getTokenizer();
		var tk = tknz.getCurrentToken(pctx);

		if (Statement.isFirst(tk)) {
			single = new Statement();
			single.parse(pctx);
		} else {
			tk = tknz.getNextToken(pctx);
			while(Statement.isFirst(tk)) {
				var stmt = new Statement();
				stmt.parse(pctx);
				body.add(stmt);
				tk = tknz.getCurrentToken(pctx);
			}
			pctx.consume(CToken.TK_RCUR, "expected '}'");
		}
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		if (single != null) {
			single.semanticCheck(pctx);
		} else {
			for(var stmt: body) {
				stmt.semanticCheck(pctx);
			}
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		if (single != null) {
			single.codeGen(pctx);
		} else {
			for(var stmt: body) {
				stmt.codeGen(pctx);
			}
		}
	}
}
