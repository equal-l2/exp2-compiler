package lang.c.parse.cond.ops;

import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.parse.expr.Expression;

public class ConditionLE extends ConditionOps {
	// conditionLE ::= LE expression

	public ConditionLE(Expression expr) {
		left = expr;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LE;
	}

	@Override
	protected void emitComparison(CParseContext pctx) {
		var o = pctx.getIOContext().getOutStream();
		var label = "LE" + pctx.getSeqId();
		o.println("\tCLR\tR2\t; ConditionLE: move false R2");
		o.println("\tCMP\tR0, R1\t; ConditionLE: let d = R1-R0");
		o.println("\tBRN\t" + label + "\t; ConditionLE: R0 <= R1 -> d >= 0 -> !(d < 0) -> !N");
		o.println("\tMOV\t#1, R2\t; ConditionLE: move true to R2");
		o.println(label + ":\t\t; ConditionLE");
		o.println("\tMOV\tR2, (R6)+\t; ConditionLE: push the result to the stack");
	}

	@Override
	protected String getElementName() {
		return "conditionLE";
	}
}
