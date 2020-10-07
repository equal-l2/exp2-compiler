package lang.c.parse.var;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;

import java.io.PrintStream;

public class Ident extends CParseRule {
	// ident ::= IDENT

	private CToken token;
	private CSymbolTableEntry entry;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		token = pctx.take();
		var name = token.getText();
		entry = pctx.getSymbolTable().search(name);
		if (entry == null) {
			pctx.fatalError(token.toExplainString() + "Identifier " + name + " is not declared");
		}
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		setCType(entry.getType());
		setConstant(entry.isConst()); // TODO: set correct constness
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		var name = token.getText();
		o.println(";;; ident starts");
		if (entry.isGlobal()) {
			o.println("\tMOV\t#" + name + ", (R6)+\t; Ident: 大域変数'" + name + "'のアドレスをスタックへ");
		} else {
			// assume R4 is frame ptr
			o.println("\tMOV\tR4, R0\t; Ident: フレームポインタをR0へ");
			o.println("\tADD\t#" + entry.getOffset() + ", R0\t; Ident: 局所変数'" + name + "'のオフセットを加算");
			o.println("\tMOV\tR0, (R6)+\t; Ident: 局所変数'" + name + "'のアドレスをスタックへ");
		}
		o.println(";;; ident completes");
	}

	public CToken getToken() {
		return token;
	}

	@Override
	public String toString() {
		return "'" + token.getText() + "' (type '" + getCType() + "')";
	}
}
