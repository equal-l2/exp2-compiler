package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.parse.decl.Declaration;

import java.io.PrintStream;
import java.util.ArrayList;

public class Program extends CParseRule {
	// program ::= { declaration } { declBlock } EOF

	private final ArrayList<Declaration> decls = new ArrayList<>();
	private final ArrayList<DeclBlock> blocks = new ArrayList<>();

	public static boolean isFirst(CToken tk) {
		return Declaration.isFirst(tk) || DeclBlock.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		CTokenizer tknz = pctx.getTokenizer();
		CToken tk = tknz.getCurrentToken(pctx);

		while (Declaration.isFirst(tk)) {
			var decl = new Declaration();
			decl.parse(pctx);
			decls.add(decl);
			tk = tknz.getCurrentToken(pctx);
		}

		while (DeclBlock.isFirst(tk)) {
			var dBlock = new DeclBlock();
			dBlock.parse(pctx);
			blocks.add(dBlock);
			tk = tknz.getCurrentToken(pctx);
		}

		if (tk.getType() != CToken.TK_EOF) {
			pctx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
		}
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		for (var decl : decls) {
			decl.semanticCheck(pctx);
		}

		for (var block : blocks) {
			block.semanticCheck(pctx);
		}
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; program starts");
		for (var decl : decls) {
			decl.codeGen(pctx);
		}
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t; Program: 最初の実行文へ");
		o.println("__START:");
		o.println("\tMOV\t#0x1000, R6\t; Program: 計算用スタック初期化");
		o.println("\tMOV\t#R6, R4\t; Program: フレームポインタ初期化");
		for (var block : blocks) {
			block.codeGen(pctx);
		}
		//o.println("\tMOV\t-(R6), R0\t; Program: 計算結果確認用");
		o.println("\tHLT\t\t\t; Program:");
		o.println("\t.END\t\t\t; Program:");
		o.println(";;; program completes");
	}
}
