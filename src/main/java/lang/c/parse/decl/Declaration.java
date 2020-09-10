package lang.c.parse.decl;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class Declaration extends CParseRule {
	// declaration ::= intDecl | constDecl

	private CParseRule decl;

	public static boolean isFirst(CToken tk) {
		return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CToken tk = pctx.getTokenizer().getCurrentToken(pctx);
		if (IntDecl.isFirst(tk)) {
			decl = new IntDecl();
		} else {
			decl = new ConstDecl();
		}
		decl.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		decl.semanticCheck(pctx);
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		decl.codeGen(pctx);
	}
}
