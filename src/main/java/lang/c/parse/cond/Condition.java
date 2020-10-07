package lang.c.parse.cond;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.cond.ops.*;
import lang.c.parse.expr.Expression;

public class Condition extends CParseRule {
	/*
	 condition ::= expression ( conditionLT | conditionLE | conditionGT | conditionGE | conditionEQ | conditionNE )
	             | trueLiteral | falseLiteral
	*/

	private CParseRule conditionOp;

	public static boolean isFirst(CToken tk) {
		return Expression.isFirst(tk) || TrueLiteral.isFirst(tk) || FalseLiteral.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		var tknz = pctx.getTokenizer();
		var tk = tknz.getCurrentToken(pctx);

		if (Expression.isFirst(tk)) {
			var expr = new Expression();
			expr.parse(pctx);

			tk = tknz.getCurrentToken(pctx);
			if (ConditionLT.isFirst(tk)) {
				conditionOp = new ConditionLT(expr);
			} else if (ConditionLE.isFirst(tk)) {
				conditionOp = new ConditionLE(expr);
			} else if (ConditionGT.isFirst(tk)) {
				conditionOp = new ConditionGT(expr);
			} else if (ConditionGE.isFirst(tk)) {
				conditionOp = new ConditionGE(expr);
			} else if (ConditionEQ.isFirst(tk)) {
				conditionOp = new ConditionEQ(expr);
			} else if (ConditionNE.isFirst(tk)) {
				conditionOp = new ConditionNE(expr);
			} else {
				pctx.fatalError(tk.toExplainString() + " unexpected operator");
			}
		} else if (TrueLiteral.isFirst(tk)) {
			conditionOp = new TrueLiteral();
		} else if (FalseLiteral.isFirst(tk)) {
			conditionOp = new FalseLiteral();
		} else {
			pctx.fatalError(tk.toExplainString() + " expected conditionOp");
		}

		conditionOp.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		conditionOp.semanticCheck(pctx);
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		conditionOp.codeGen(pctx);
	}
}
