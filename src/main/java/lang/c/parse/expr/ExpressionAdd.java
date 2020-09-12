package lang.c.parse.expr;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;
import lang.c.parse.BinaryOp;
import lang.c.parse.term.Term;

import java.io.PrintStream;

public class ExpressionAdd extends BinaryOp<Term> {
	// expressionAdd ::= '+' term

	public ExpressionAdd(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}

	@Override
	protected CType getType() {
		CType lhs = left.getCType();
		CType rhs = right.getCType();
		if (lhs.isCType(CType.T_int) && rhs.isCType(CType.T_int)) {
			return CType.getCType(CType.T_int);
		} else if (lhs.isCType(CType.T_pint) && rhs.isCType(CType.T_int)) {
			return CType.getCType(CType.T_pint);
		} else if (lhs.isCType(CType.T_int) && rhs.isCType(CType.T_pint)) {
			return CType.getCType(CType.T_pint);
		} else {
			return CType.getCType(CType.T_err);
		}
	}

	@Override
	protected void typeError(CParseContext pctx) throws FatalErrorException {
		pctx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType() + "]と右辺の型[" + right.getCType() + "]は足せません");
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		op = pctx.take();
		pctx.expect(Term::isFirst, "+の後ろはtermです");
		right = new Term();
		right.parse(pctx);
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		left.codeGen(pctx);        // 左部分木のコード生成を頼む
		right.codeGen(pctx);        // 右部分木のコード生成を頼む
		o.println("\tMOV\t-(R6), R0\t; ExpressionAdd: ２数を取り出して、足し、積む<" + op + ">");
		o.println("\tMOV\t-(R6), R1\t; ExpressionAdd:");
		o.println("\tADD\tR1, R0\t; ExpressionAdd:");
		o.println("\tMOV\tR0, (R6)+\t; ExpressionAdd:");
	}
}
