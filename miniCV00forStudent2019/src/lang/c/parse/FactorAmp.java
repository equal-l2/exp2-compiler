package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class FactorAmp extends CParseRule {
	// factorAmp ::= AMP number
	CParseRule number;

	public FactorAmp(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.err.println("FACTORAMP");
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);
		if (Number.isFirst(tk)) {
			number = new Number(pcx);
			number.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "&の後ろはNumberです");
		}
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		number.semanticCheck(pcx);
		setCType(CType.getCType(CType.T_pint));
		setConstant(number.isConstant());    // number は常に定数
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factorAmp starts");
		if (number != null) {
			number.codeGen(pcx);
		}
		o.println(";;; factorAmp completes");
	}
}
