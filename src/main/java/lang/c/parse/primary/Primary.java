package lang.c.parse.primary;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.parse.variable.Variable;

import java.io.PrintStream;

public class Primary extends CParseRule {
	// primary ::= primaryMult | variable

	private CParseRule primary;

	public static boolean isFirst(CToken tk) {
		return PrimaryMult.isFirst(tk) || Variable.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer tknz = pcx.getTokenizer();
		CToken tk = tknz.getCurrentToken(pcx);
		if (PrimaryMult.isFirst(tk)) {
			primary = new PrimaryMult();
		} else if (Variable.isFirst(tk)) {
			primary = new Variable();
		} else {
			pcx.fatalError(tk.toExplainString() + "expected primaryMult | variable");
		}
		primary.parse(pcx);
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		primary.semanticCheck(pcx);
		setCType(primary.getCType());
		setConstant(primary.isConstant());
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		primary.codeGen(pcx);
	}
}
