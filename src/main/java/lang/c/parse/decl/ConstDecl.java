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
	private String name;
	private String value;
	private CSymbolTableEntry entry;
	private CToken eq;

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

		eq = pctx.consume(CToken.TK_ASSIGN, "expected '='");

		if (tknz.getCurrentToken(pctx).getType() == CToken.TK_AMP) {
			rhs_type = CType.getCType(CType.T_pint);
			tknz.getNextToken(pctx);
		}

		value = pctx.consume(CToken.TK_NUM, "expected NUM").getText();

		final int size = 1; // only implemented for int and int*

		var table = pctx.getSymbolTable();
		var ret = table.register(name, lhs_type, size, true);
		if (ret != null) {
			pctx.fatalError(ident.toExplainString() + " Identifier \"" + name + "\" is already declared");
		}
		entry = table.search(name);

		// パース段階で左辺・右辺の型は確定するのでこの時点で型エラーを出すこともできるが
		// 型が違ってもsyntax上は問題なく、semanticの問題だし
		// statementAssignとのparityも考えてsemanticCheckまでエラーは待たせる
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		if (!lhs_type.equals(rhs_type)) {
			pctx.fatalError(eq.toExplainString() + " cannot initialize '" + name + "' (type '" + lhs_type + "') with type '" + rhs_type + "'");
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		if (entry.isGlobal()) {
			o.println(name + ":");
			o.println("\t.WORD\t" + value + "\t; ConstDecl");
		} else {
			// assume R4 is frame ptr
			o.println("\tMOV\tR4, R3\t; ConstDecl: スタックポインタのアドレスをR3へ");
			o.println("\tMOV\t#" + entry.getOffset() + ", R3\t; ConstDecl: 局所変数のオフセットを加算");
			o.println("\tMOV\t#" + value + ", (R3)\t; ConstDecl: 局所変数に値を代入");
		}
	}
}
