package lang.c.parse.decl;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;
import java.util.ArrayList;

public class IntDecl extends CParseRule {
	// intDecl ::= INT declItem { COMMA declItem } SEMI

	private final ArrayList<DeclItem> intDecl = new ArrayList<>();

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CTokenizer tknz = pctx.getTokenizer();
		tknz.getNextToken(pctx);

		while (true) {
			pctx.expect(DeclItem::isFirst, "expected declItem");
			var item = new DeclItem();
			item.parse(pctx);
			intDecl.add(item);

			if (tknz.getCurrentToken(pctx).getType() != CToken.TK_COMMA) break;
			tknz.getNextToken(pctx);
		}

		pctx.consume(CToken.TK_SEMI, "expected ';'");
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		for (CParseRule decl : intDecl) {
			decl.semanticCheck(pctx);
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		for (CParseRule decl : intDecl) {
			decl.codeGen(pctx);
		}
	}
}

class DeclItem extends CParseRule {
	// declItem ::= [ MULT ] IDENT [ LBRA NUM RBRA ]

	private CType type = CType.getCType(CType.T_int);
	private String size;
	private String name;

	public static boolean isFirst(CToken tk) {
		int ty = tk.getType();
		return ty == CToken.TK_MULT || ty == CToken.TK_IDENT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CTokenizer tknz = pctx.getTokenizer();
		if (tknz.getCurrentToken(pctx).getType() == CToken.TK_MULT) {
			type = CType.getCType(CType.T_pint);
			tknz.getNextToken(pctx);
		}

		CToken ident = pctx.consume(CToken.TK_IDENT, "expected IDENT");
		name = ident.getText();

		if (tknz.getCurrentToken(pctx).getType() == CToken.TK_LBRA) {
			CToken tk = tknz.getNextToken(pctx);
			size = pctx.consume(CToken.TK_NUM, "expected NUM").getText();

			pctx.consume(CToken.TK_RBRA, "expected ']'");

			type = type.toArrayType();
		}

		var ret = pctx.getSymbolTable().register(name, new CSymbolTableEntry(type, false));
		if (ret != null) {
			pctx.fatalError(ident.toExplainString() + " Identifier \"" + name + "\" is already declared");
		}
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		// noop
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(name + ":");
		if (type.isArray()) {
			o.println("\t.BLKW\t" + size + "\t; IntDecl");
		} else {
			o.println("\t.WORD\t0\t; IntDecl");
		}
	}
}
