package lang.c.parse.prim.rvar;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

import java.io.PrintStream;

public class Number extends CParseRule {
	// number ::= NUM
	private String num;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_NUM;
	}

	public void parse(CParseContext pctx) throws FatalErrorException {
		num = pctx.take().getText();
	}

	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		setCType(CType.getCType(CType.T_int));
		setConstant(true);
	}

	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println("\tMOV\t#" + num + ", (R6)+\t; Number: 数 " + num + " を積む");
	}
}
