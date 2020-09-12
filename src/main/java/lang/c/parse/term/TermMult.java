package lang.c.parse.term;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.BinaryOp;
import lang.c.parse.factor.Factor;

import java.io.PrintStream;

public class TermMult extends BinaryOp<Factor> {
	// termMult ::= '*' factor

	@Override
	protected CType getType() {
		CType lhs = left.getCType();
		CType rhs = right.getCType();
		if (lhs.isCType(CType.T_int) && rhs.isCType(CType.T_int)) {
			return CType.getCType(CType.T_int);
		} else {
			return CType.getCType(CType.T_err);
		}
	}

	@Override
	protected void typeError(CParseContext pctx) throws FatalErrorException {
		pctx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType() + "]と右辺の型[" + right.getCType() + "]は乗算できません");
	}

	public TermMult(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		op = pctx.take();
		pctx.expect(Factor::isFirst, "*の後ろはfactorです");
		right = new Factor();
		right.parse(pctx);
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		left.codeGen(pctx);        // 左部分木のコード生成を頼む
		right.codeGen(pctx);        // 右部分木のコード生成を頼む

		// MULにはスタックに載っている値を使って計算してもらう
		// 結果もスタックに載せてもらう
		o.println("\tJSR\tMUL\t;");
	}
}
