package lang.c;

import java.util.HashMap;

public class CTokenRule extends HashMap<String, Integer> {
	public CTokenRule() {
		put("int", CToken.TK_INT);
		put("const", CToken.TK_CONST);
	}
}
