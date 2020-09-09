package lang.c;

import lang.IOContext;
import lang.ParseContext;

public class CParseContext extends ParseContext {
	public CParseContext(IOContext ioCtx, CTokenizer tknz) {
		super(ioCtx, tknz);
	}

	@Override
	public CTokenizer getTokenizer() {
		return (CTokenizer) super.getTokenizer();
	}

	private int seqNo;

	public int getSeqId() {
		return ++seqNo;
	}
}
