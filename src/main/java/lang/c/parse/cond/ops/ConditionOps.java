package lang.c.parse.cond.ops;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CType;
import lang.c.parse.BinaryOp;
import lang.c.parse.expr.Expression;

import java.io.PrintStream;

public abstract class ConditionOps extends BinaryOp<Expression> {
	// conditionOp ::= Op expression

	// Emit comparison asm using lhs (on R0) and rhs (on R1)
	// The result must be pushed on the stack in form of 0/1 (false/true)
	protected abstract void emitComparison(CParseContext pctx);

	@Override
	protected void typeError(CParseContext pctx) throws FatalErrorException {
		pctx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType() + "]と右辺の型[" + right.getCType() + "]は比較できません");
	}

	@Override
	protected void initRight(CParseContext pctx) throws FatalErrorException {
		pctx.expect(Expression::isFirst, "expected expression");
		right = new Expression();
	}

	@Override
	protected CType getType() {
		CType lhs = left.getCType();
		CType rhs = right.getCType();
		if (lhs.equals(rhs)) {
			if (lhs.isCType(CType.T_int) || lhs.isCType(CType.T_pint)) {
				return CType.getCType(CType.T_bool);
			}
		}
		return CType.getCType(CType.T_err);
	}

	@Override
	protected void emitBiOpAsm(CParseContext pctx) {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println("\tMOV\t-(R6), R0\t; ConditionOps");
		o.println("\tMOV\t-(R6), R1\t; ConditionOps");
		emitComparison(pctx);
	}
}
