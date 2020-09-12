package lang.c.parse.expr;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.BinaryOp;
import lang.c.parse.term.Term;

import java.io.PrintStream;

public class ExpressionSub extends BinaryOp<Term> {
	// expressionSub ::= '-' term

	@Override
	protected CType getType() {
		CType lhs = left.getCType();
		CType rhs = right.getCType();
		if (lhs.isCType(CType.T_int) && rhs.isCType(CType.T_int)) {
			return CType.getCType(CType.T_int);
		}
		else if (lhs.isCType(CType.T_pint) && rhs.isCType(CType.T_int)) {
			return CType.getCType(CType.T_pint);
		}
		else if (lhs.isCType(CType.T_pint) && rhs.isCType(CType.T_pint)) {
			return CType.getCType(CType.T_int);
		}
		else {
			return CType.getCType(CType.T_err);
		}
	}

	@Override
	protected void typeError(CParseContext pctx) throws FatalErrorException {
		pctx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType() + "]から右辺の型[" + right.getCType() + "]は引けません");
	}

	public ExpressionSub(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		op = pctx.take();
		pctx.expect(Term::isFirst, "-の後ろはtermです");
		right = new Term();
		right.parse(pctx);
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		left.codeGen(pctx);  // 左部分木のコード生成を頼む
		right.codeGen(pctx); // 右部分木のコード生成を頼む

		// R0に結果が載るように、ポップ順をExpressionAddとは逆にする
		o.println("\tMOV\t-(R6), R1\t; ExpressionSub: ２数を取り出して、引き、積む<" + op + ">");
		o.println("\tMOV\t-(R6), R0\t; ExpressionSub:");
		o.println("\tSUB\tR1, R0\t; ExpressionSub:");
		o.println("\tMOV\tR0, (R6)+\t; ExpressionSub:");
	}
}
