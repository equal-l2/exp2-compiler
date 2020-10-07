package lang.c.parse.factor;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.parse.prim.UnsignedFactor;

import java.io.PrintStream;

public class Factor extends CParseRule {
	// factor ::= plusFactor | minusFactor | unsignedFactor
	private CParseRule factor;

	public static boolean isFirst(CToken tk) {
		return PlusFactor.isFirst(tk) || MinusFactor.isFirst(tk) || UnsignedFactor.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CToken tk = pctx.getTokenizer().getCurrentToken(pctx);
		if (PlusFactor.isFirst(tk)) {
			factor = new PlusFactor();
		} else if (MinusFactor.isFirst(tk)) {
			factor = new MinusFactor();
		} else if (UnsignedFactor.isFirst(tk)) {
			factor = new UnsignedFactor();
		} else {
			pctx.fatalError(tk.toExplainString() + " expected plusFactor | minusFactor | unsignedFactor");
		}
		factor.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		factor.semanticCheck(pctx);
		setCType(factor.getCType());
		setConstant(factor.isConstant());
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		factor.codeGen(pctx);
		o.println(";;; factor completes");
	}
}

