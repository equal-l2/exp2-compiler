package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

public abstract class BinaryOp<Operand extends CParseRule> extends CParseRule {
	// binaryOp ::= OP operand

	protected CToken op;
	protected CParseRule left;
	protected Operand right;

	protected abstract CType getType();

	protected abstract void typeError(CParseContext pctx) throws FatalErrorException;

	@Override
	public abstract void parse(CParseContext pctx) throws FatalErrorException;

	@Override
	public abstract void codeGen(CParseContext pctx) throws FatalErrorException;

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		left.semanticCheck(pctx);
		right.semanticCheck(pctx);

		CType t = getType();
		if (t.isCType(CType.T_err)){
			typeError(pctx);
		}
		setCType(t);
		setConstant(left.isConstant() && right.isConstant());
	}
}
