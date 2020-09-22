package lang.c.parse.var;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

import java.io.PrintStream;

public class Variable extends CParseRule {
	// variable ::= ident [ array ]

	private Ident ident;
	private Array array;

	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		pctx.expect(Ident::isFirst, "expected ident");
		ident = new Ident();
		ident.parse(pctx);

		CToken tk = pctx.getTokenizer().getCurrentToken(pctx);
		if (Array.isFirst(tk)) {
			array = new Array();
			array.parse(pctx);
		}
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		ident.semanticCheck(pctx);
		if (array != null) {
			array.semanticCheck(pctx);
			CType ty = ident.getCType();
			if (!ty.isArray()) {
				// NOTE: 要件によりポインタはindexできないようにしてある
				pctx.fatalError("cannot index type " + ty);
			}
			setCType(ty.deref());
		} else {
			CType ty = ident.getCType();
			if (!(ty.isCType(CType.T_int) || ty.isCType(CType.T_pint))) {
				// NOTE: 要件により配列型変数はindexされた形でしか出現できない
				pctx.fatalError("expected scalar types, found " + ty);
			}
			setCType(ty);
		}
		setConstant(ident.isConstant());
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; variable starts");
		ident.codeGen(pctx);
		o.println("\tMOV\t-(R6), R0\t; Identのアドレスをポップ");
		if (array != null) {
			array.codeGen(pctx);
			o.println("\tMOV\t-(R6), R1\t; Arrayのexprの値をスタックへ");
			o.println("\tADD\tR1, R0   \t; Variableのアドレス値を計算");
		}
		o.println("\tMOV\tR0, (R6)+\t; 計算したアドレスをスタックへ");
		o.println(";;; variable completes");
	}
}
