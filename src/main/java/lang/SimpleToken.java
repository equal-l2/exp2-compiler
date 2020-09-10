package lang;

public class SimpleToken extends Token {
	public static final int TK_IDENT = 0;    // 識別子（ラベル）
	public static final int TK_NUM = 1;    // 数値
	public static final int TK_EOF = -1;    // （ファイルの終端記号）
	public static final int TK_ILL = -2;    // 未定義トークン

	protected final int type;                // 上のどのトークンか
	private final String text;                // 切り出したトークンの綴り
	private final int lineNo;                // このトークンがあった行
	private final int colNo;                // このトークンがあった桁

	@Override
	public int getType() {
		return type;
	}

	@Override
	public String getTypeString() {
		return switch (type) {
			case TK_IDENT -> "IDENT";
			case TK_NUM -> "NUM";
			case TK_EOF -> "EOF";
			case TK_ILL -> "ILL";
			default -> "Unknown";
		};
	}

	@Override
	public String getText() {
		return text == null ? "(null)" : text;
	}

	@Override
	public int getLineNo() {
		return lineNo;
	}

	@Override
	public int getColumnNo() {
		return colNo;
	}

	public int getIntValue() {
		return Integer.decode(text);
	}

	public SimpleToken(int type, int lineNo, int colNo, String s) {
		this.type = type;
		this.lineNo = lineNo;
		this.colNo = colNo;
		text = s;
	}
}
