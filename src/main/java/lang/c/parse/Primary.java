package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

import java.io.PrintStream;

public class Primary extends CParseRule {
	// primary ::= primaryMult | variable

	private CParseRule primary;

	public static boolean isFirst(CToken tk) {
		return PrimaryMult.isFirst(tk) || Variable.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer tknz = pcx.getTokenizer();
		CToken tk = tknz.getCurrentToken(pcx);
		if (PrimaryMult.isFirst(tk)) {
			primary = new PrimaryMult();
		} else if (Variable.isFirst(tk)) {
			primary = new Variable();
		} else {
			pcx.fatalError(tk.toExplainString() + "expected primaryMult | variable");
		}
		primary.parse(pcx);
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		primary.semanticCheck(pcx);
		setCType(primary.getCType());
		setConstant(false); // TODO: constならtrue?
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		primary.codeGen(pcx);
		o.println("\tMOV\t-(R6), R0\t; アドレスをポップ");
		o.println("\tMOV\t(R0), (R6)+\t; 値をスタックへ");
		o.println(";;; primary completes");

	}
}
