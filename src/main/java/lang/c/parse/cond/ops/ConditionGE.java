package lang.c.parse.cond.ops;

import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.parse.expr.Expression;

public class ConditionGE extends ConditionOps {
	// conditionGE ::= GE expression

	public ConditionGE(Expression expr) {
		left = expr;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GE;
	}

	@Override
	protected void emitComparison(CParseContext pctx) {
		var o = pctx.getIOContext().getOutStream();
		var label = "GE" + pctx.getSeqId();
		o.println("\tCLR\tR2\t; ConditionGE: move false to R2");
		o.println("\tCMP\tR1, R0\t; ConditionGE: let b = R0-R1");
		o.println("\tBRN\t" + label + "\t; ConditionGE: R0 >= R1 -> b >= 0 -> !N ");
		o.println("\tMOV\t#1, R2\t; ConditionGE: move true to R2");
		o.println(label + ":\t\t; ConditionGE");
		o.println("\tMOV\tR2, (R6)+\t; ConditionGE: push the result to the stack");
	}
}
