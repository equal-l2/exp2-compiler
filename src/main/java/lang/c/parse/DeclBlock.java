package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.decl.Declaration;
import lang.c.parse.stmt.Statement;

import java.io.PrintStream;
import java.util.ArrayList;

public class DeclBlock extends CParseRule {
	// declBlock ::= LCUR { declaration } { statement } RCUR

	private final ArrayList<Declaration> decls = new ArrayList<>();
	private final ArrayList<Statement> stmts = new ArrayList<>();
	private int offset;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LCUR;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		var tknz = pctx.getTokenizer();
		var table = pctx.getSymbolTable();

		table.enterLocal();
		CToken tk = tknz.getNextToken(pctx);
		while (Declaration.isFirst(tk)) {
			var decl = new Declaration();
			decl.parse(pctx);
			decls.add(decl);
			tk = tknz.getCurrentToken(pctx);
		}

		offset = table.getOffset();

		while (Statement.isFirst(tk)) {
			var stmt = new Statement();
			stmt.parse(pctx);
			stmts.add(stmt);
			tk = tknz.getCurrentToken(pctx);
		}

		pctx.consume(CToken.TK_RCUR, "expected '}'");
		table.exitLocal();
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		for (var decl : decls) {
			decl.semanticCheck(pctx);
		}

		for (var stmt : stmts) {
			stmt.semanticCheck(pctx);
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; declBlock starts");
		if (offset != 0) {
			o.println("\tADD\t#" + offset + ", R6\t; DeclBlock: 局所変数領域を確保");
		}
		for (var decl : decls) {
			decl.codeGen(pctx);
		}
		for (var stmt : stmts) {
			stmt.codeGen(pctx);
		}
		if (offset != 0) {
			o.println("\tSUB\t#" + offset + ", R6\t; ConstDecl: 局所変数領域を開放");
		}
		o.println(";;; declBlock completes");
	}
}
