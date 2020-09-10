package lang;

public interface ParseRule<PCTX> {
	void parse(PCTX pctx) throws FatalErrorException;

	void semanticCheck(PCTX pctx) throws FatalErrorException;

	void codeGen(PCTX pctx) throws FatalErrorException;
}
