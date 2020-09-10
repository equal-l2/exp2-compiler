package lang.c.parse.factor;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.parse.primary.UnsignedFactor;

public class PlusFactor extends CParseRule {
	// plusFactor ::= PLUS unsignedFactor
	private CParseRule uFactor;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // '+' を読み飛ばす
		if (UnsignedFactor.isFirst(tk)) {
			uFactor = new UnsignedFactor();
		} else {
			pcx.fatalError(tk.toExplainString() + "expected unsignedFactor");
		}
		uFactor.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		uFactor.semanticCheck(pcx);
		setCType(uFactor.getCType());
		setConstant(uFactor.isConstant());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		uFactor.codeGen(pcx);
	}
}
