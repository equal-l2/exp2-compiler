package lang.c.parse.factor;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.CType;
import lang.c.parse.UnaryOps;
import lang.c.parse.prim.UnsignedFactor;

public class PlusFactor extends UnaryOps<UnsignedFactor> {
	// plusFactor ::= PLUS unsignedFactor

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}

	@Override
	protected CType getType() {
		var t = operand.getCType();
		if (t.isCType(CType.T_int)) {
			return t;
		} else {
			return CType.getErrorType();
		}
	}

	@Override
	protected void initOperand(CParseContext pctx) throws FatalErrorException {
		pctx.expect(UnsignedFactor::isFirst, "expected unsignedFactor");
		operand = new UnsignedFactor();
	}

	@Override
	protected void emitUnary(CParseContext pctx) {
		System.err.println("ICE: this should not be called");
	}

	@Override
	protected String getElementName() {
		return "plusFactor";
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		operand.codeGen(pctx);
	}
}
