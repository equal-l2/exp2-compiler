package lang.c.parse.primary;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class Number extends CParseRule {
	// number ::= NUM
	private CToken num;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_NUM;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		num = ct.getCurrentToken(pcx);
		ct.getNextToken(pcx); // トークンを進める
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		setCType(CType.getCType(CType.T_int));
		setConstant(true);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		String numStr = num.getText();
		o.println("\tMOV\t#" + numStr + ", (R6)+\t; Number: 数 " + numStr + " を積む<" + num.toExplainString() + ">");
	}
}
