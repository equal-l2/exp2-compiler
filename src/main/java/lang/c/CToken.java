package lang.c;

import lang.SimpleToken;

public class CToken extends SimpleToken {
	public static final int TK_PLUS = 2;    // +
	public static final int TK_MINUS = 3;   // -
	public static final int TK_AMP = 4;     // &
	public static final int TK_MULT = 5;    // *
	public static final int TK_DIV = 6;     // /
	public static final int TK_LPAR = 7;    // (
	public static final int TK_RPAR = 8;    // )
	public static final int TK_LBRA = 9;    // [
	public static final int TK_RBRA = 10;   // ]
	public static final int TK_ASSIGN = 11; // =
	public static final int TK_SEMI = 12;   // ;
	public static final int TK_CONST = 13;  // const
	public static final int TK_INT = 14;    // int
	public static final int TK_COMMA = 15;  // ,
	public static final int TK_EQ = 16; // ==
	public static final int TK_NE = 17; // !=
	public static final int TK_LT = 18; // <
	public static final int TK_LE = 19; // <=
	public static final int TK_GT = 20; // >
	public static final int TK_GE = 21; // >=
	public static final int TK_TRUE = 22; // true
	public static final int TK_FALSE = 23; // false
	public static final int TK_LCUR = 24;    // {
	public static final int TK_RCUR = 25;   // }
	public static final int TK_INPUT = 26; // input
	public static final int TK_OUTPUT = 27; // output
	public static final int TK_WHILE = 28; // while
	public static final int TK_IF = 29; // if
	public static final int TK_ELSE = 30; // else

	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}

	@Override
	public String getTypeString() {
		return switch (type) {
			case TK_PLUS -> "PLUS";
			case TK_MINUS -> "MINUS";
			case TK_AMP -> "AMP";
			case TK_MULT -> "MULT";
			case TK_DIV -> "DIV";
			case TK_LPAR -> "LPAR";
			case TK_RPAR -> "RPAR";
			case TK_LBRA -> "LBRA";
			case TK_RBRA -> "RBRA";
			case TK_ASSIGN -> "ASSIGN";
			case TK_SEMI -> "SEMI";
			case TK_CONST -> "CONST";
			case TK_INT -> "INT";
			case TK_COMMA -> "COMMA";
			case TK_EQ -> "EQ";
			case TK_NE -> "NE";
			case TK_LT -> "LT";
			case TK_LE -> "LE";
			case TK_GT -> "GT";
			case TK_GE -> "GE";
			case TK_TRUE -> "TRUE";
			case TK_FALSE -> "FALSE";
			case TK_LCUR -> "LCUR";
			case TK_RCUR -> "RCUR";
			case TK_INPUT -> "INPUT";
			case TK_OUTPUT -> "OUTPUT";
			case TK_WHILE -> "WHILE";
			case TK_IF -> "IF";
			case TK_ELSE -> "ELSE";
			default -> super.getTypeString();
		};
	}
}
