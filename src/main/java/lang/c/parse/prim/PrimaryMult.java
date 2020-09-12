package lang.c.parse.prim;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.var.Variable;

import java.io.PrintStream;

public class PrimaryMult extends CParseRule {
	// primaryMult ::= MULT variable

	private CToken op;
	private Variable variable;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	@Override
	public void parse(CParseContext pctx) throws FatalErrorException {
		op = pctx.take();
		pctx.expect(Variable::isFirst, "expected variable");
		variable = new Variable();
		variable.parse(pctx);
	}

	@Override
	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		variable.semanticCheck(pctx);
		CType ty = variable.getCType();
		if (ty.isCType(CType.T_pint)) {
			setCType(CType.getCType(CType.T_int));
		} else {
			// NOTE: int*のderefのみ実装
			pctx.fatalError(op.toExplainString() + "cannot dereference " + ty);
		}
		setConstant(variable.isConstant()); // FIXME: get correct constness
	}

	@Override
	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; primaryMult starts");
		variable.codeGen(pctx);
		o.println("\tMOV\t-(R6), R0\t; ポインタのアドレスをポップ");
		o.println("\tMOV\t(R0), (R6)+\t; ポインタの内容(アドレス)をスタックへ");
		o.println(";;; primaryMult completes");
	}
}
