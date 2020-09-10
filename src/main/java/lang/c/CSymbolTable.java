package lang.c;

import lang.SymbolTable;

public class CSymbolTable extends SymbolTable<CSymbolTableEntry> {

	@Override
	public CSymbolTableEntry register(String name, CSymbolTableEntry symbolTableEntry) {
		return put(name, symbolTableEntry);
	}

	@Override
	public CSymbolTableEntry search(String name) {
		return get(name);
	}
}
