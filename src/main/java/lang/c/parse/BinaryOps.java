package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

public abstract class BinaryOps<Operand extends CParseRule> extends CParseRule {
	// binaryOps ::= OP operand

	protected CToken op;
	protected CParseRule left;
	protected Operand right;

	protected abstract CType getType();

	protected void typeError(CParseContext pctx) throws FatalErrorException {
		var opExplain = op.toExplainString();
		var opText = op.getText();
		var rType = right.getCType();
		var lType = left.getCType();
		pctx.fatalError(opExplain + " invalid operand types for binary operator '" + opText + "' ('" + lType + "' and '" + rType + "')");
	}

	// Check if the next token can be parsed as `Operand`,
	// assign `Operand` object to `right` if so.
	protected abstract void initRight(CParseContext pctx) throws FatalErrorException;

	// emit asm for the operator using lhs and rhs on the stack
	// the result must be also on the stack
	protected abstract void emitBiOpAsm(CParseContext pctx);

	// get name of the element class
	// this is used to indicate the beginning and the ending of the element in asm
	protected abstract String getElementName();

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
		var o = pctx.getIOContext().getOutStream();
		var name = getElementName();

		o.println(";;; " + name + " starts");
		left.codeGen(pctx);  // 左部分木のコード生成を頼む
		right.codeGen(pctx); // 右部分木のコード生成を頼む
		emitBiOpAsm(pctx);
		o.println(";;; " + name + " completes");
	}
}
