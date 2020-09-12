package lang.c.parse.factor;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.prim.UnsignedFactor;

public class PlusFactor extends CParseRule {
	// plusFactor ::= PLUS unsignedFactor
	private UnsignedFactor uFactor;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		pctx.take();
		pctx.expect(UnsignedFactor::isFirst, "expected unsignedFactor");
		uFactor = new UnsignedFactor();
		uFactor.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		uFactor.semanticCheck(pctx);
		setCType(uFactor.getCType());
		setConstant(uFactor.isConstant());
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		uFactor.codeGen(pctx);
	}
}
