package lang.c.parse.expr;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.term.Term;

import java.io.PrintStream;

public class ExpressionAdd extends CParseRule {
	// expressionAdd ::= '+' term
	private CToken op;
	private final CParseRule left;
	private Term right;

	public ExpressionAdd(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}

	public void parse(CParseContext pctx) throws FatalErrorException {
		op = pctx.take();
		pctx.expect(Term::isFirst, "+の後ろはtermです");
		right = new Term();
		right.parse(pctx);
	}

	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		// 足し算の型計算規則
		final int[][] rule = {
				//(右辺)   T_err         T_int         T_pint
				{CType.T_err, CType.T_err, CType.T_err},    // T_err
				{CType.T_err, CType.T_int, CType.T_pint},   // T_int
				{CType.T_err, CType.T_pint, CType.T_err},    // T_pint
		};
		left.semanticCheck(pctx);
		right.semanticCheck(pctx);
		int lt = left.getCType().getType();  // +の左辺の型
		int rt = right.getCType().getType(); // +の右辺の型
		int nt = rule[lt][rt];                  // 規則による型計算
		if (nt == CType.T_err) {
			pctx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType() + "]と右辺の型[" + right.getCType() + "]は足せません");
		}
		setCType(CType.getCType(nt));
		setConstant(left.isConstant() && right.isConstant());    // +の左右両方が定数のときだけ定数
	}

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
