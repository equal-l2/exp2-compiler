package lang.c.parse.term;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.factor.Factor;

import java.io.PrintStream;

public class TermDiv extends CParseRule {
	// termDiv ::= '*' factor
	private CToken op;
	private final CParseRule left;
	private CParseRule right;

	public TermDiv(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// /の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor();
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "/の後ろはfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 引き算の型計算規則
		final int[][] rule = {
				//(右辺)     T_err        T_int        T_pint
				{CType.T_err, CType.T_err, CType.T_err},    // T_err
				{CType.T_err, CType.T_int, CType.T_err},    // T_int
				{CType.T_err, CType.T_err, CType.T_err},    // T_pint
		};
		left.semanticCheck(pcx);
		right.semanticCheck(pcx);
		int lt = left.getCType().getType();
		int rt = right.getCType().getType();
		int nt = rule[lt][rt];                        // 規則による型計算
		if (nt == CType.T_err) {
			pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType() + "]は右辺の型[" + right.getCType() + "]で除算できません");
		}
		setCType(CType.getCType(nt));
		setConstant(left.isConstant() && right.isConstant());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		left.codeGen(pcx);        // 左部分木のコード生成を頼む
		right.codeGen(pcx);        // 右部分木のコード生成を頼む

		// DIVにはスタックに載っている値を使って計算してもらう
		// 結果もスタックに載せてもらう
		o.println("\tJSR\tDIV\t;");
	}
}
