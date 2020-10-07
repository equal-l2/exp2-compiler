package lang.c;

import lang.FatalErrorException;
import lang.IOContext;
import lang.ParseContext;

import java.util.function.Predicate;

public class CParseContext extends ParseContext {
	private CSymbolTable symbolTable = new CSymbolTable();
	private int seqNo;

	public CParseContext(IOContext ioCtx, CTokenizer tknz) {
		super(ioCtx, tknz);
	}

	@Override
	public CTokenizer getTokenizer() {
		return (CTokenizer) super.getTokenizer();
	}

	public CSymbolTable getSymbolTable() {
		return symbolTable;
	}

	public int getSeqId() {
		return ++seqNo;
	}

	public CToken expect(Predicate<CToken> predicate, String err) throws FatalErrorException {
		CToken tk = getTokenizer().getCurrentToken(this);
		if (!predicate.test(tk)) {
			fatalError(tk.toExplainString() + " " + err);
		}
		return tk;
	}

	public CToken consume(int type, String err) throws FatalErrorException {
		CToken tk = expect(t -> t.getType() == type, err);
		getTokenizer().getNextToken(this);
		return tk;
	}

	public CToken take() throws FatalErrorException {
		CTokenizer tknz = getTokenizer();
		CToken tk = tknz.getCurrentToken(this);
		tknz.getNextToken(this);
		return tk;
	}
}
