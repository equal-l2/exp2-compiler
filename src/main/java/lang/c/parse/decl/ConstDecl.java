package lang.c.parse.decl;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;
import java.util.ArrayList;

public class ConstDecl extends CParseRule {
	// constDecl ::= CONST INT constItem { COMMA constItem } SEMI

	private final ArrayList<ConstItem> constDecl = new ArrayList<>();

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CONST;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CTokenizer tknz = pctx.getTokenizer();
		tknz.getNextToken(pctx);
		pctx.consume(CToken.TK_INT, "expected \"int\"");

		while (true) {
			pctx.expect(ConstItem::isFirst, "expected constItem");
			var item = new ConstItem();
			item.parse(pctx);
			constDecl.add(item);

			if (tknz.getCurrentToken(pctx).getType() != CToken.TK_COMMA) break;
			tknz.getNextToken(pctx);
		}

		pctx.consume(CToken.TK_SEMI, "expected ';'");
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		for (CParseRule decl : constDecl) {
			decl.semanticCheck(pctx);
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		for (CParseRule decl : constDecl) {
			decl.codeGen(pctx);
		}
	}
}

class ConstItem extends CParseRule {
	// constItem ::= [ MULT ] IDENT ASSIGN [ AMP ] NUM

	private CType lhs_type = CType.getCType(CType.T_int);
	private CType rhs_type = CType.getCType(CType.T_int);
	private String value;
	private String name;

	public static boolean isFirst(CToken tk) {
		int ty = tk.getType();
		return ty == CToken.TK_MULT || ty == CToken.TK_IDENT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CTokenizer tknz = pctx.getTokenizer();
		if (tknz.getCurrentToken(pctx).getType() == CToken.TK_MULT) {
			lhs_type = CType.getCType(CType.T_pint);
			tknz.getNextToken(pctx);
		}

		CToken ident = pctx.consume(CToken.TK_IDENT, "expected IDENT");
		name = ident.getText();

		pctx.consume(CToken.TK_ASSIGN, "expected '='");

		if (tknz.getCurrentToken(pctx).getType() == CToken.TK_AMP) {
			rhs_type = CType.getCType(CType.T_pint);
			tknz.getNextToken(pctx);
		}

		value = pctx.consume(CToken.TK_NUM, "expected NUM").getText();

		var ret = pctx.getSymbolTable().register(name, new CSymbolTableEntry(lhs_type, true));
		if (ret != null) {
			pctx.fatalError(ident.toExplainString() + " Identifier \"" + name + "\" is already declared");
		}
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		if (!lhs_type.equals(rhs_type)) {
			pctx.fatalError("cannot assign " + rhs_type + " to " + lhs_type);
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(name + ":");
		o.println("\t.WORD\t" + value + "\t; ConstDecl");
	}
}
