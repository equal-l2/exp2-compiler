package lang.c.parse.primary;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.variable.Variable;

import java.io.PrintStream;

public class FactorAmp extends CParseRule {
	// factorAmp ::= AMP (number | variable)
	/*
		本来
			factorAmp ::= AMP (number | primary)
		だが、primaryの中身を見るのは面倒だし、この文法でもLL(1)のはずなので
		何か困るまではこの形で行く

		つぶやき: 教科書の意図としては、variableがlvalueで、primaryがrvalueなのかな……
	*/
	private CParseRule factorAmp;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);
		if (Number.isFirst(tk)) {
			factorAmp = new Number();
		} else if (Variable.isFirst(tk)) {
			factorAmp = new Variable();
		} else {
			pcx.fatalError(tk.toExplainString() + "&の後ろはNumberかVariableです");
		}
		factorAmp.parse(pcx);
	}

	@Override
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		factorAmp.semanticCheck(pcx);
		CType ty = factorAmp.getCType();
		if (!ty.isCType(CType.T_int)) {
			pcx.fatalError("cannot take the address of " + ty);
		}
		setCType(CType.getCType(CType.T_pint));
		setConstant(true); // factorAmpはrvalue
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factorAmp starts");
		// factorAmp object will generate address
		factorAmp.codeGen(pcx);
		o.println(";;; factorAmp completes");
	}
}
