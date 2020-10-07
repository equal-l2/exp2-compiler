package lang.c.parse.prim.rvar;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;
import lang.c.parse.var.Variable;

import java.io.PrintStream;

public class FactorAmp extends CParseRule {
	// factorAmp ::= AMP (number | variable)
	/*
		本来
			factorAmp ::= AMP (number | primary)
		だが、primaryの中身を見るのは面倒だし、この文法でもLL(1)のはずなので
		何か困るまではこの形で行く
	*/

	private CToken op;
	private CParseRule factorAmp;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		var tknz = pctx.getTokenizer();
		op = tknz.getCurrentToken(pctx);
		CToken tk = tknz.getNextToken(pctx);
		if (Number.isFirst(tk)) {
			factorAmp = new Number();
		} else if (Variable.isFirst(tk)) {
			factorAmp = new Variable();
		} else {
			pctx.fatalError(tk.toExplainString() + " &の後ろはNumberかVariableです");
		}
		factorAmp.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		factorAmp.semanticCheck(pctx);
		CType ty = factorAmp.getCType();
		if (!ty.isCType(CType.T_int)) {
			pctx.fatalError(
					op.toExplainString() + " cannot take the address of type '" + ty + "'",
					"only address of 'int' type variable is available");
		}
		setCType(CType.getCType(CType.T_pint));
		setConstant(true); // factorAmpはrvalue
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; factorAmp starts");
		// factorAmp object will generate address
		factorAmp.codeGen(pctx);
		o.println(";;; factorAmp completes");
	}
}
