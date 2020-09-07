package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class Term extends CParseRule {
	// term ::= factor { termMult | termDiv }
	private CParseRule term;

	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule factor = new Factor();
		factor.parse(pcx);

		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		CParseRule list;
		while (true) {
			if (TermMult.isFirst(tk)) {
				list = new TermMult(factor);
			} else if (TermDiv.isFirst(tk)) {
				list = new TermDiv(factor);
			} else {
				break;
			}
			list.parse(pcx);
			factor = list;
			tk = ct.getCurrentToken(pcx);
		}
		term = factor;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (term != null) {
			term.semanticCheck(pcx);
			this.setCType(term.getCType());
			this.setConstant(term.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		if (term != null) {
			term.codeGen(pcx);
		}
		o.println(";;; term completes");
	}
}

class TermMult extends CParseRule {
	// termMult ::= '*' factor
	private CToken op;
	private final CParseRule left;
	private CParseRule right;

	public TermMult(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// *の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor();
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "*の後ろはfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 引き算の型計算規則
		final int[][] s = {
				//(右辺)     T_err        T_int        T_pint
				{CType.T_err, CType.T_err, CType.T_err},    // T_err
				{CType.T_err, CType.T_int, CType.T_err},    // T_int
				{CType.T_err, CType.T_err, CType.T_err},    // T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType();
			int rt = right.getCType().getType();
			int nt = s[lt][rt];
			if (nt == CType.T_err) {
				pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は乗算できません");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx);        // 左部分木のコード生成を頼む
			right.codeGen(pcx);        // 右部分木のコード生成を頼む

			// MULにはスタックに載っている値を使って計算してもらう
			// 結果もスタックに載せてもらう
			o.println("\tJSR\tMUL\t;");
		}
	}
}

class TermDiv extends CParseRule {
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
		final int[][] s = {
				//(右辺)     T_err        T_int        T_pint
				{CType.T_err, CType.T_err, CType.T_err},    // T_err
				{CType.T_err, CType.T_int, CType.T_err},    // T_int
				{CType.T_err, CType.T_err, CType.T_err},    // T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType();
			int rt = right.getCType().getType();
			int nt = s[lt][rt];                        // 規則による型計算
			if (nt == CType.T_err) {
				pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]は右辺の型[" + right.getCType().toString() + "]で除算できません");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx);        // 左部分木のコード生成を頼む
			right.codeGen(pcx);        // 右部分木のコード生成を頼む

			// DIVにはスタックに載っている値を使って計算してもらう
			// 結果もスタックに載せてもらう
			o.println("\tJSR\tDIV\t;");
		}
	}
}
