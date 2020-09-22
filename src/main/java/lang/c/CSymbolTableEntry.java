package lang.c;

import lang.SymbolTableEntry;

public class CSymbolTableEntry extends SymbolTableEntry {
	private final CType type;
	private final int size;
	private final boolean isConst;
	private final boolean isGlobal;
	private final int offset;

	public CSymbolTableEntry(CType type, int size, boolean isConst, boolean isGlobal, int offset) {
		this.type = type;
		this.size = size;
		this.isConst = isConst;
		this.isGlobal = isGlobal;
		this.offset = offset;
	}

	@Override
	public String toExplainString() {
		return "{ size: " + size + ", type: " + type + ", isConst: " + isConst + ", isGlobal: " + isGlobal + ", offset: " + offset + " }";
	}

	public CType getType() {
		return type;
	}

	public int getOffset() {
		return offset;
	}

	public int getSize() {
		return size;
	}

	public boolean isConst() {
		return isConst;
	}

	public boolean isGlobal() {
		return isGlobal;
	}
}
