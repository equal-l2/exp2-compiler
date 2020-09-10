package lang.c;

import lang.SymbolTableEntry;

public class CSymbolTableEntry extends SymbolTableEntry {
	private final CType type;
	private final boolean isConst;

	public CSymbolTableEntry(CType type, boolean isConst) {
		this.type = type;
		this.isConst= isConst;
	}

	@Override
	public String toExplainString() {
		return "CSymbolTableEntry { type: " + type + ", isConst: " + isConst + " }";
	}

	public CType getType() { return type; }

	public boolean isConst() {
		return isConst;
	}
}
