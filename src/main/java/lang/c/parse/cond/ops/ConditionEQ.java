package lang.c.parse.cond.ops;

import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.parse.expr.Expression;

public class ConditionEQ extends ConditionOps {
	// conditionEQ ::= EQ expression

	public ConditionEQ(Expression expr) {
		left = expr;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_EQ;
	}

	@Override
	protected void emitComparison(CParseContext pctx) {
		var o = pctx.getIOContext().getOutStream();
		var label = "EQ" + pctx.getSeqId();
		o.println("\tMOV\t#1, R2\t; ConditionEQ: move true to R2");
		o.println("\tCMP\tR0, R1\t; ConditionEQ: let d = R1-R0");
		o.println("\tBRZ\t" + label + "\t; ConditionEQ: R0 == R1 -> d == 0 -> Z");
		o.println("\tCLR\tR2\t; ConditionEQ: move false to R2");
		o.println(label + ":\t\t; ConditionEQ");
		o.println("\tMOV\tR2, (R6)+\t; ConditionEQ: push the result to the stack");
	}
}
