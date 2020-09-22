package lang.c.parse.var;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;

import java.io.PrintStream;

public class Ident extends CParseRule {
	// ident ::= IDENT

	private String name;
	private CSymbolTableEntry entry;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CToken tk = pctx.take();
		name = tk.getText();
		entry = pctx.getSymbolTable().search(name);
		if (entry == null) {
			pctx.fatalError(tk.toExplainString() + "Identifier " + name + " is not declared");
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
		if (entry.isGlobal()) {
			o.println("\tMOV\t#" + name + ", (R6)+\t; Ident: 大域変数'" + name + "'のアドレスをスタックへ");
		} else {
			// assume R4 is frame ptr
			o.println("\tMOV\tR4, R3\t; ConstDecl: スタックポインタのアドレスをR3へ");
			o.println("\tMOV\t#" + entry.getOffset() + ", R3\t; ConstDecl: 局所変数'" + name + "'のオフセットを加算");
			o.println("\tMOV\tR3, (R6)+\t; ConstDecl: 局所変数'" + name + "'のアドレスをスタックへ");
		}
	}
}
