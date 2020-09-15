package lang.c.parse.cond.ops;

import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.parse.expr.Expression;

public class ConditionGT extends ConditionOps {
	// conditionGT ::= GT expression

	public ConditionGT(Expression expr) {
		left = expr;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GT;
	}

	@Override
	protected void emitComparison(CParseContext pctx) {
		var o = pctx.getIOContext().getOutStream();
		var label = "GT" + pctx.getSeqId();
		o.println("\tMOV\t#1, R2\t; ConditionOps: move true to R2");
		o.println("\tCMP\tR0, R1\t; ConditionGT: let d = R1-R0");
		o.println("\tBRN\t" + label + "\t; ConditionGT: R0 > R1 -> d < 0 -> N");
		o.println("\tCLR\tR2\t; ConditionGT: move false to R2");
		o.println(label + ":\t\t; ConditionGT");
		o.println("\tMOV\tR2, (R6)+\t; ConditionGT: push the result to the stack");
	}

	@Override
	protected String getElementName() {
		return "conditionGT";
	}
}
