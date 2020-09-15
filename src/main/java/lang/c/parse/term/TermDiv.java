package lang.c.parse.term;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;
import lang.c.parse.BinaryOp;
import lang.c.parse.factor.Factor;

import java.io.PrintStream;

public class TermDiv extends BinaryOp<Factor> {
	// termDiv ::= '*' factor

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
	protected void typeError(CParseContext pctx) throws FatalErrorException {
		pctx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType() + "]は右辺の型[" + right.getCType() + "]で除算できません");
	}

	@Override
	protected void initRight(CParseContext pctx) throws FatalErrorException {
		pctx.expect(Factor::isFirst, "/の後ろはfactorです");
		right = new Factor();
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		left.codeGen(pctx);        // 左部分木のコード生成を頼む
		right.codeGen(pctx);        // 右部分木のコード生成を頼む

	}

	@Override
	protected void emitBiOpAsm(CParseContext pctx) {
		// DIVにはスタックに載っている値を使って計算してもらう
		// 結果もスタックに載せてもらう
		pctx.getIOContext().getOutStream().println("\tJSR\tDIV\t;");
	}
}
