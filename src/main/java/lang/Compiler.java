package lang;


public interface Compiler<Pctx> {
	void semanticCheck(Pctx pcx) throws FatalErrorException;

	void codeGen(Pctx pcx) throws FatalErrorException;
}

