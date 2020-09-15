package lang.c;

import lang.SymbolTable;

public class CSymbolTable extends SymbolTable<CSymbolTableEntry> {

	@Override
	public CSymbolTableEntry register(String name, CSymbolTableEntry e) {
		return put(name, e);
	}

	@Override
	public CSymbolTableEntry search(String name) {
		return get(name);
	}
}
