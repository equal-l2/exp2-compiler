package lang.c.parse.primary;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.variable.Variable;

import java.io.PrintStream;

public class PrimaryMult extends CParseRule {
	// primaryMult ::= MULT variable

	private CToken op;
	private CParseRule variable;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer tknz = pcx.getTokenizer();
		op = tknz.getCurrentToken(pcx);
		CToken tk = tknz.getNextToken(pcx);
		if (Variable.isFirst(tk)) {
			variable = new Variable();
			variable.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "expected variable");
		}
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		variable.semanticCheck(pcx);
		CType ty = variable.getCType();
		if (!ty.isCType(CType.T_pint)) {
			// NOTE: int*のderefのみ実装
			pcx.fatalError(op.toExplainString() + "cannot dereference " + ty);
		}
		setCType(CType.getCType(CType.T_int));
		setConstant(false); // FIXME: get correct constness
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primaryMult starts");
		variable.codeGen(pcx);
		o.println("\tMOV\t-(R6), R0\t; ポインタのアドレスをポップ");
		o.println("\tMOV\t(R0), (R6)+\t; ポインタの内容(アドレス)をスタックへ");
		o.println(";;; primaryMult completes");
	}
}
