package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class Ident extends CParseRule {
	// ident ::= IDENT

	private CToken ident;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		ident = ct.getCurrentToken(pcx);
		ct.getNextToken(pcx); // トークンを進める
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		setCType(CType.getCType(CType.T_aint)); // TODO: set correct type
		setConstant(false);
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		String name = ident.getText();
		o.println("\tMOV\t#" + name + ", (R6)+\t; Ident: 変数\""+name+"\"のアドレスをスタックへ");
	}
}
