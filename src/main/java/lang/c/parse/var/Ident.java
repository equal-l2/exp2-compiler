package lang.c.parse.var;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;

import java.io.PrintStream;

public class Ident extends CParseRule {
	// ident ::= IDENT

	private String ident;
	private CSymbolTableEntry info;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CToken tk = pctx.take();
		ident = tk.getText();
		info = pctx.getSymbolTable().search(ident);
		if (info == null) {
			pctx.fatalError(tk.toExplainString() + "Identifier " + ident + " is not declared");
		}
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		setCType(info.getType());
		setConstant(info.isConst()); // TODO: set correct constness
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println("\tMOV\t#" + ident + ", (R6)+\t; Ident: 変数\"" + ident + "\"のアドレスをスタックへ");
	}
}
