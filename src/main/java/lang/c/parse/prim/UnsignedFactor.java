package lang.c.parse.prim;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.parse.expr.Expression;
import lang.c.parse.prim.rvar.FactorAmp;
import lang.c.parse.prim.rvar.Number;

import java.io.PrintStream;

public class UnsignedFactor extends CParseRule {
	// unsignedFactor ::= factorAmp | number | LPAR expression RPAR | addressToValue
	private CParseRule uFactor;

	public static boolean isFirst(CToken tk) {
		return FactorAmp.isFirst(tk) || Number.isFirst(tk) || tk.getType() == CToken.TK_LPAR || AddressToValue.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer tknz = pctx.getTokenizer();
		CToken tk = tknz.getCurrentToken(pctx);
		if (tk.getType() == CToken.TK_LPAR) {
			tknz.getNextToken(pctx); // '(' を読み飛ばす
			pctx.expect(Expression::isFirst, "expected expression");
			uFactor = new Expression();
			uFactor.parse(pctx);

			pctx.consume(CToken.TK_RPAR, "expected ')'");
		} else {
			if (FactorAmp.isFirst(tk)) {
				uFactor = new FactorAmp();
			} else if (Number.isFirst(tk)) {
				uFactor = new Number();
			} else if (AddressToValue.isFirst(tk)) {
				uFactor = new AddressToValue();
			} else {
				pctx.fatalError(tk.toExplainString() + " expected (factorAmp | number | LPAR expression RPAR | addressToValue)");
			}
			uFactor.parse(pctx);
		}
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		uFactor.semanticCheck(pctx);
		setCType(uFactor.getCType());
		setConstant(uFactor.isConstant());
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; unsignedFactor starts");
		uFactor.codeGen(pctx);
		o.println(";;; unsignedFactor completes");
	}
}