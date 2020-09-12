package lang.c;

import lang.LL1;
import lang.ParseRule;

public abstract class CParseRule implements ParseRule<CParseContext>, LL1<CToken> {
	// この節点の（推測される）型
	private CType ctype;
	// この節点は定数を表しているか？
	private boolean isConstant;

	public CType getCType() {
		return ctype;
	}

	public void setCType(CType ctype) {
		this.ctype = ctype;
	}

	public boolean isConstant() {
		return isConstant;
	}

	public void setConstant(boolean isConstant) {
		this.isConstant = isConstant;
	}
}
