package lang.c.parse.variable;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class Variable extends CParseRule {
	// variable ::= ident [ array ]

	private CParseRule ident;
	private CParseRule array;

	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer tknz = pcx.getTokenizer();
		CToken tk = tknz.getCurrentToken(pcx);
		if (Ident.isFirst(tk)) {
			ident = new Ident();
			ident.parse(pcx);
		} else {
			pcx.fatalError("expected ident");
		}

		tk = tknz.getCurrentToken(pcx);
		if (Array.isFirst(tk)) {
			array = new Array();
			array.parse(pcx);
		}
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		ident.semanticCheck(pcx);
		if (array != null) {
			array.semanticCheck(pcx);
			CType ty = ident.getCType();
			if (!ty.isArray()) {
				// NOTE: 要件によりポインタはindexできないようにしてある
				pcx.fatalError("cannot index type " + ty);
			}
			setCType(ty.deref());
		} else {
			CType ty = ident.getCType();
			if (!(ty.isCType(CType.T_int) || ty.isCType(CType.T_pint))) {
				// NOTE: 要件により配列型変数はindexされた形でしか出現できない
				pcx.fatalError("expected scalar types, found " + ty);
			}
			setCType(ty);
		}
		setConstant(ident.isConstant());
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; variable starts");
		ident.codeGen(pcx);
		if (array != null) {
			array.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; Arrayのexprの値をスタックへ");
		} else {
			o.println("\tMOV\t#0, R0\t; ");
		}
		o.println("\tMOV\t-(R6), R1\t; Identのアドレスをポップ");
		o.println("\tADD\tR1, R0   \t; Variableのアドレス値を計算");
		o.println("\tMOV\tR0, (R6)+\t; 計算したアドレスをスタックへ");
		o.println(";;; variable completes");
	}
}
