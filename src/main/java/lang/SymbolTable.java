package lang;

import java.util.HashMap;

public abstract class SymbolTable<E extends SymbolTableEntry> extends HashMap<String, E> {
	// 登録
	public abstract E register(String name, E e);

	// 検索
	public abstract E search(String name);

	// 全体表示
	public void show() {
//		System.out.println("--- Symbol Table ---");
		for (String label : keySet()) {
			E e = get(label);
			if (e == null) {
				System.out.println(label + "\t= (null) [未定義]");
			} else {
				System.out.println(label + "\t= " + e.toExplainString());
			}
		}
//		System.out.println("------");
	}
}
