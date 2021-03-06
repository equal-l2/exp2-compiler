package lang.c.parse.term;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;
import lang.c.parse.BinaryOps;
import lang.c.parse.factor.Factor;

public class TermDiv extends BinaryOps<Factor> {
	// termDiv ::= '/' factor

	public TermDiv(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
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
	protected void initRight(CParseContext pctx) throws FatalErrorException {
		pctx.expect(Factor::isFirst, "/の後ろはfactorです");
		right = new Factor();
	}

	@Override
	protected void emitBiOpAsm(CParseContext pctx) {
		// DIVはスタックに載っている値を使って計算し、結果をR0で返す
		pctx.getIOContext().getOutStream().println("\tJSR\tDIV\t; termDiv: 計算サブルーチンを呼ぶ");
		pctx.getIOContext().getOutStream().println("\tSUB\t#2, R6\t; termDiv: スタックに載った引数を片付ける");
		pctx.getIOContext().getOutStream().println("\tMOV\tR0, (R6)+\t; termDiv: スタックに結果を載せる");
	}

	@Override
	protected String getElementName() {
		return "termDiv";
	}
}
