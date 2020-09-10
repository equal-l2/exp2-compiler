package lang.c.parse.expression;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.term.Term;

import java.io.PrintStream;

public class ExpressionSub extends CParseRule {
	// expressionSub ::= '-' term
	private CToken op;
	private final CParseRule left;
	private CParseRule right;

	public ExpressionSub(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// -の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Term.isFirst(tk)) {
			right = new Term();
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "-の後ろはtermです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 引き算の型計算規則
		final int[][] rule = {
				//(右辺)     T_err        T_int        T_pint
				{CType.T_err, CType.T_err, CType.T_err},    // T_err
				{CType.T_err, CType.T_int, CType.T_err},    // T_int
				{CType.T_err, CType.T_pint, CType.T_int},    // T_pint
		};
		left.semanticCheck(pcx);
		right.semanticCheck(pcx);
		int lt = left.getCType().getType();  // -の左辺の型
		int rt = right.getCType().getType(); // -の右辺の型
		int nt = rule[lt][rt];                  // 規則による型計算
		if (nt == CType.T_err) {
			pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType() + "]から右辺の型[" + right.getCType() + "]は引けません");
		}
		setCType(CType.getCType(nt));
		setConstant(left.isConstant() && right.isConstant());    // -の左右両方が定数のときだけ定数
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		left.codeGen(pcx);  // 左部分木のコード生成を頼む
		right.codeGen(pcx); // 右部分木のコード生成を頼む

		// R0に結果が載るように、ポップ順をExpressionAddとは逆にする
		o.println("\tMOV\t-(R6), R1\t; ExpressionSub: ２数を取り出して、引き、積む<" + op + ">");
		o.println("\tMOV\t-(R6), R0\t; ExpressionSub:");
		o.println("\tSUB\tR1, R0\t; ExpressionSub:");
		o.println("\tMOV\tR0, (R6)+\t; ExpressionSub:");
	}
}
