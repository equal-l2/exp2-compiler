package lang.c;

public class CType {
	public static final int T_err   = 0; // 型エラー
	public static final int T_int   = 1; // int
	public static final int T_pint  = 2; // int*
	public static final int T_aint  = 3; // int[]
	public static final int T_apint = 4; // int*[]

	private static final CType[] typeArray = {
			new CType(T_err, "error"),
			new CType(T_int, "int"),
			new CType(T_pint, "int*"),
			new CType(T_aint, "int[]"),
			new CType(T_apint, "int*[]"),
	};

	private final int type;
	private final String typename;

	private CType(int type, String s) {
		this.type = type;
		typename = s;
	}

	public static CType getCType(int type) {
		return typeArray[type];
	}

	public boolean isCType(int t) {
		return t == type;
	}

	public boolean isCType(CType t) {
		return isCType(t.getType());
	}

	public static boolean isArray(int t) {
		return t == T_aint || t == T_apint;
	}

	public boolean isArray() {
		return isArray(type);
	}

	public boolean isIndexable() {
		return isArray() || type == T_pint;
	}

	public CType deref() {
		int ty = switch (type) {
			case T_pint, T_aint -> T_int;
			case T_apint -> T_pint;
			default -> T_err;
		};
		return typeArray[ty];
	}

	public int getType() {
		return type;
	}

	public String toString() {
		return typename;
	}
}
