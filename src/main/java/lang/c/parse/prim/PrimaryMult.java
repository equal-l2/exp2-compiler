package lang.c.parse.prim;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.CType;
import lang.c.parse.UnaryOps;
import lang.c.parse.var.Variable;

import java.io.PrintStream;

public class PrimaryMult extends UnaryOps<Variable> {
	// primaryMult ::= MULT variable

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	@Override
	protected CType getType() {
		var t = operand.getCType();
		if (t.isCType(CType.T_pint)) {
			return CType.getCType(CType.T_int);
		} else {
			// NOTE: int*のderefのみ実装
			return CType.getErrorType();
		}
	}

	@Override
	protected void initOperand(CParseContext pctx) throws FatalErrorException {
		pctx.expect(Variable::isFirst, "expected variable");
		operand = new Variable();
	}

	@Override
	protected void emitUnary(CParseContext pctx) {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println("\tMOV\t-(R6), R0\t; ポインタのアドレスをポップ");
		o.println("\tMOV\t(R0), (R6)+\t; ポインタの内容(アドレス)をスタックへ");
	}

	@Override
	protected String getElementName() {
		return "primaryMult";
	}
}
