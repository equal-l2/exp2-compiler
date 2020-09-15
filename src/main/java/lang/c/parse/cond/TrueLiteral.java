package lang.c.parse.cond;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class TrueLiteral extends CParseRule {
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_TRUE;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		// skip this token
		pctx.getTokenizer().getNextToken(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		// noop
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		pctx.getIOContext().getOutStream().println("\tMOV\t#1, (R6)+\t; true");
	}
}
