package lang.c.parse.term;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.factor.Factor;

import java.io.PrintStream;

public class TermDiv extends CParseRule {
	// termDiv ::= '*' factor
	private CToken op;
	private final CParseRule left;
	private Factor right;

	public TermDiv(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
	}

	public void parse(CParseContext pctx) throws FatalErrorException {
		op = pctx.take();
		pctx.expect(Factor::isFirst, "/の後ろはfactorです");
		right = new Factor();
		right.parse(pctx);
	}

	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		// 除算の型計算規則
		final int[][] rule = {
				//(右辺)     T_err        T_int        T_pint
				{CType.T_err, CType.T_err, CType.T_err},    // T_err
				{CType.T_err, CType.T_int, CType.T_err},    // T_int
				{CType.T_err, CType.T_err, CType.T_err},    // T_pint
		};
		left.semanticCheck(pctx);
		right.semanticCheck(pctx);
		int lt = left.getCType().getType();
		int rt = right.getCType().getType();
		int nt = rule[lt][rt];                        // 規則による型計算
		if (nt == CType.T_err) {
			pctx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType() + "]は右辺の型[" + right.getCType() + "]で除算できません");
		}
		setCType(CType.getCType(nt));
		setConstant(left.isConstant() && right.isConstant());
	}

	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		left.codeGen(pctx);        // 左部分木のコード生成を頼む
		right.codeGen(pctx);        // 右部分木のコード生成を頼む

		// DIVにはスタックに載っている値を使って計算してもらう
		// 結果もスタックに載せてもらう
		o.println("\tJSR\tDIV\t;");
	}
}
