package lang.c.parse.cond.ops;

import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.parse.expr.Expression;

public class ConditionLT extends ConditionOps {
	// conditionLT ::= LT expression

	public ConditionLT(Expression expr) {
		left = expr;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LT;
	}

	@Override
	protected void emitComparison(CParseContext pctx) {
		var o = pctx.getIOContext().getOutStream();
		var label = "LT" + pctx.getSeqId();
		o.println("\tMOV\t#1, R2\t; ConditionLT: move true to R2");
		o.println("\tCMP\tR1, R0\t; ConditionLT: let b = R0-R1");
		o.println("\tBRN\t" + label + "\t; ConditionLT: R0 < R1 -> b < 0 -> N");
		o.println("\tCLR\tR2\t; ConditionLT: move false R2");
		o.println(label + ":\t\t; ConditionLT");
		o.println("\tMOV\tR2, (R6)+\t; ConditionLT: push the result to the stack");
	}
}
