package lang.c.parse.prim;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.var.Variable;

public class Primary extends CParseRule {
	// primary ::= primaryMult | variable

	private CParseRule primary;

	public static boolean isFirst(CToken tk) {
		return PrimaryMult.isFirst(tk) || Variable.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CToken tk = pctx.getTokenizer().getCurrentToken(pctx);
		if (PrimaryMult.isFirst(tk)) {
			primary = new PrimaryMult();
		} else if (Variable.isFirst(tk)) {
			primary = new Variable();
		} else {
			pctx.fatalError(tk.toExplainString() + "expected primaryMult | variable");
		}
		primary.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		primary.semanticCheck(pctx);
		setCType(primary.getCType());
		setConstant(primary.isConstant());
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		primary.codeGen(pctx);
	}
}
