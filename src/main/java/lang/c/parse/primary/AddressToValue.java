package lang.c.parse.primary;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.io.PrintStream;

public class AddressToValue extends CParseRule {
	// addressToValue ::= Primary
	private CParseRule prim;

	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		prim = new Primary();
		prim.parse(pcx);
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		prim.semanticCheck(pcx);
		setCType(prim.getCType());
		setConstant(prim.isConstant());
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; addressToValue starts");
		prim.codeGen(pcx);
		o.println("\tMOV\t-(R6), R0\t; アドレスをポップ");
		o.println("\tMOV\t(R0), (R6)+\t; 値をスタックへ");
		o.println(";;; addressToValue completes");
	}
}
