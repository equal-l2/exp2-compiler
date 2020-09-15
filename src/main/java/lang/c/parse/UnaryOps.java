package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

public abstract class UnaryOps<Operand extends CParseRule> extends CParseRule {
	// unaryOps ::= OP operand

	protected CToken op;
	protected Operand operand;

	protected abstract CType getType();

	protected void typeError(CParseContext pctx) throws FatalErrorException {
		var opExplain = op.toExplainString();
		var opText = op.getText();
		var type = operand.getCType();
		pctx.fatalError(opExplain + " invalid operand type for unary operator '" + opText + "' ('" + type + "')");
	}

	// Check if the next token can be parsed as `Operand`,
	// assign `Operand` object to `operand` if so.
	protected abstract void initOperand(CParseContext pctx) throws FatalErrorException;

	// emit asm for the operator using the operand on the stack
	// the result must be also on the stack
	protected abstract void emitUnary(CParseContext pctx);

	// get name of the element class
	// this is used to indicate the beginning and the ending of the element in asm
	protected abstract String getElementName();

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		op = pctx.take();
		initOperand(pctx);
		operand.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		operand.semanticCheck(pctx);

		CType t = getType();
		if (t.isCType(CType.T_err)) {
			typeError(pctx);
		}
		setCType(t);
		setConstant(operand.isConstant());
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		var o = pctx.getIOContext().getOutStream();
		var name = getElementName();

		o.println(";;; " + name + " starts");
		operand.codeGen(pctx);
		emitUnary(pctx);
		o.println(";;; " + name + " completes");
	}
}
