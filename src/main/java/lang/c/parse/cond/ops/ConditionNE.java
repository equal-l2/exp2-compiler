package lang.c.parse.cond.ops;

import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.parse.expr.Expression;

public class ConditionNE extends ConditionOps {
	// conditionNE ::= NE expression

	public ConditionNE(Expression expr) {
		left = expr;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_NE;
	}

	@Override
	protected void emitComparison(CParseContext pctx) {
		var o = pctx.getIOContext().getOutStream();
		var label = "NE" + pctx.getSeqId();
		o.println("\tCLR\tR2\t; ConditionNE: move false to R2");
		o.println("\tCMP\tR0, R1\t; ConditionNE: let d = R1-R0");
		o.println("\tBRZ\t" + label + "\t; ConditionNE: R0 != R1 -> d != 0 -> !Z");
		o.println("\tMOV\t#1, R2\t; ConditionNE: move true to R2");
		o.println(label + ":\t\t; ConditionNE");
		o.println("\tMOV\tR2, (R6)+\t; ConditionNE: push the result to the stack");
	}

	@Override
	protected String getElementName() {
		return "conditionNE";
	}
}
