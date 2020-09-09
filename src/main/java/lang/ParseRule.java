package lang;

public interface ParseRule<Pctx> {
	void parse(Pctx pcx) throws FatalErrorException;

	void semanticCheck(Pctx pcx) throws FatalErrorException;

	void codeGen(Pctx pcx) throws FatalErrorException;
}
