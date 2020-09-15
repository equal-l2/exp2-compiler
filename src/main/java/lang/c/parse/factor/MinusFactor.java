package lang.c.parse.factor;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.CType;
import lang.c.parse.UnaryOps;
import lang.c.parse.prim.UnsignedFactor;

import java.io.PrintStream;

public class MinusFactor extends UnaryOps<UnsignedFactor> {
	// minusFactor ::= MINUS unsignedFactor

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
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
		pctx.expect(UnsignedFactor::isFirst, " expected unsignedFactor");
		operand = new UnsignedFactor();
	}

	@Override
	protected void emitUnary(CParseContext pctx) {
		PrintStream o = pctx.getIOContext().getOutStream();
		/* -x を得るために 0-x を行う */
		o.println("\tMOV\t-(R6), R1;");
		o.println("\tMOV\t#0, R0;");
		o.println("\tSUB\tR1, R0");
		o.println("\tMOV\tR0, (R6)+");
	}

	@Override
	protected String getElementName() {
		return "minusFactor";
	}
}
