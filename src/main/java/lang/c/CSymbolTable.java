package lang.c;

import lang.SymbolTable;

public class CSymbolTable {
	private static final class OneSymbolTable extends SymbolTable<CSymbolTableEntry> {
		@Override
		public CSymbolTableEntry register(String name, CSymbolTableEntry e) {
			return put(name, e);
		}

		@Override
		public CSymbolTableEntry search(String name) {
			return get(name);
		}
	}

	private OneSymbolTable global = new OneSymbolTable();
	private OneSymbolTable local = new OneSymbolTable();
	private boolean isGlobal = true;
	private int localOffset = 0; //TODO: is this correct?

	public CSymbolTableEntry register(String name, CType type, int size, boolean isConst) {
		final int offset = isGlobal ? 0 : localOffset + 1;
		final CSymbolTableEntry entry = new CSymbolTableEntry(type, size, isConst, isGlobal, offset);
		CSymbolTableEntry ret;
		if (isGlobal) {
			ret = global.put(name, entry);
		} else {
			ret = local.put(name, entry);
			localOffset += size;
		}
		return ret;
	}

	public CSymbolTableEntry search(String name) {
		CSymbolTableEntry ret = local.get(name);
		if (ret == null) {
			ret = global.get(name);
		}
		return ret;
	}

	public void show() {
		global.show();
		local.show();
	}

	public void enterLocal() {
		isGlobal = false;
	}

	public void exitLocal() {
		isGlobal = true;
		local.clear();
		localOffset = 0;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	public int getOffset() {
		return localOffset;
	}
}
