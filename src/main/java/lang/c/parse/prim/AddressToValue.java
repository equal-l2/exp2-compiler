package lang.c.parse.prim;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.io.PrintStream;

public class AddressToValue extends CParseRule {
	// addressToValue ::= Primary
	private Primary prim;

	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		prim = new Primary();
		prim.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		prim.semanticCheck(pctx);
		setCType(prim.getCType());
		setConstant(prim.isConstant());
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; addressToValue starts");
		prim.codeGen(pctx);
		o.println("\tMOV\t-(R6), R0\t; アドレスをポップ");
		o.println("\tMOV\t(R0), (R6)+\t; 値をスタックへ");
		o.println(";;; addressToValue completes");
	}
}
