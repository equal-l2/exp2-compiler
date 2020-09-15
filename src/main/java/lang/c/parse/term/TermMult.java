package lang.c.parse.term;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;
import lang.c.parse.BinaryOp;
import lang.c.parse.factor.Factor;

public class TermMult extends BinaryOp<Factor> {
	// termMult ::= '*' factor

	public TermMult(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

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

	@Override
	protected void initRight(CParseContext pctx) throws FatalErrorException {
		pctx.expect(Factor::isFirst, "*の後ろはfactorです");
		right = new Factor();
	}

	@Override
	protected void emitBiOpAsm(CParseContext pctx) {
		// MULにはスタックに載っている値を使って計算してもらう
		// 結果もスタックに載せてもらう
		pctx.getIOContext().getOutStream().println("\tJSR\tMUL\t;");
	}
}
