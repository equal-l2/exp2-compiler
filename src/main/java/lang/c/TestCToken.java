package lang.c;

import lang.FatalErrorException;
import lang.IOContext;

public class TestCToken {
	public static void main(String[] args) {
		String inFile = args[0]; // 適切なファイルを絶対パスで与えること
		IOContext ioCtx = new IOContext(inFile, System.out, System.err);
		CTokenizer tknz = new CTokenizer(new CTokenRule());
		CParseContext pcx = new CParseContext(ioCtx, tknz);
		try {
			CTokenizer ct = pcx.getTokenizer();
			CToken tk = ct.getNextToken(pcx);
			if (TestTokenizer.isFirst(tk)) {
				CParseRule program = new TestTokenizer();
				program.parse(pcx);
				program.codeGen(pcx);
			}
		} catch (FatalErrorException e) {
			e.printStackTrace();
		}
	}

	private static class TestTokenizer extends CParseRule {
		//		program  ::= { token } EOF
		public static boolean isFirst(CToken tk) {
			return true;
		}

		public void parse(CParseContext pctx) {
			CToken tk = pctx.getTokenizer().getCurrentToken(pctx);
			while (tk.getType() != CToken.TK_EOF) {
				if (tk.getType() == CToken.TK_NUM) {
					pctx.getIOContext().getOutStream().println("Token=" + tk.toExplainString() + "type=" + tk.getTypeString() + " value=" + tk.getIntValue());
				} else {
					pctx.getIOContext().getOutStream().println("Token=" + tk.toExplainString() + "type=" + tk.getTypeString());
				}
				tk = pctx.getTokenizer().getNextToken(pctx);
			}
		}

		public void semanticCheck(CParseContext pctx) throws FatalErrorException {
			// do nothing
		}

		public void codeGen(CParseContext pctx) throws FatalErrorException {
			// do nothing
		}
	}
}

