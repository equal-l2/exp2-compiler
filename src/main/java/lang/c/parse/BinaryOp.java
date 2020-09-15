package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;
import lang.c.parse.term.Term;

public abstract class BinaryOp<Operand extends CParseRule> extends CParseRule {
	// binaryOp ::= OP operand

	protected CToken op;
	protected CParseRule left;
	protected Operand right;

	protected abstract CType getType();

	protected abstract void typeError(CParseContext pctx) throws FatalErrorException;

	// Check if the next token can be parsed as `Operand`,
	// assign `Operand` object to `right` if so.
	protected abstract void initRight(CParseContext pctx) throws FatalErrorException;

	// emit asm for the operator using lhs(R0) and rhs(R1)
	// the result must be on the stack
	protected abstract void emitBiOpAsm(CParseContext pctx);

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		op = pctx.take();
		initRight(pctx);
		right.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		left.semanticCheck(pctx);
		right.semanticCheck(pctx);

		CType t = getType();
		if (t.isCType(CType.T_err)) {
			typeError(pctx);
		}
		setCType(t);
		setConstant(left.isConstant() && right.isConstant());
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		left.codeGen(pctx);  // 左部分木のコード生成を頼む
		right.codeGen(pctx); // 右部分木のコード生成を頼む
		emitBiOpAsm(pctx);
	}
}
