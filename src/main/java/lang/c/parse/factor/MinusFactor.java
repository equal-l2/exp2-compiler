package lang.c.parse.factor;

import lang.FatalErrorException;
import lang.c.*;
import lang.c.parse.prim.UnsignedFactor;

import java.io.PrintStream;

public class MinusFactor extends CParseRule {
	// minusFactor ::= MINUS unsignedFactor
	private CToken op;
	private UnsignedFactor uFactor;

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}

	public void parse(CParseContext pctx) throws FatalErrorException {
		op = pctx.take();
		pctx.expect(UnsignedFactor::isFirst, "expected unsignedFactor");
		uFactor = new UnsignedFactor();
		uFactor.parse(pctx);
	}

	public void semanticCheck(CParseContext pctx) throws FatalErrorException {
		uFactor.semanticCheck(pctx);
		CType t = uFactor.getCType();
		if (t.isCType(CType.T_int)) {
			setCType(t);
		} else {
			pctx.fatalError(op.toExplainString() + "型[" + uFactor.getCType() + "]に単項マイナス演算子は適用できません");
		}
		setConstant(uFactor.isConstant());
	}

	public void codeGen(CParseContext pctx) throws FatalErrorException {
		PrintStream o = pctx.getIOContext().getOutStream();
		o.println(";;; minusFactor starts");
		uFactor.codeGen(pctx); // 式部分のコードを生成

		/* -x を得るために 0-x を行う */
		o.println("\tMOV\t-(R6), R1;");
		o.println("\tMOV\t#0, R0;");
		o.println("\tSUB\tR1, R0");
		o.println("\tMOV\tR0, (R6)+");
		o.println(";;; minusFactor completes");
	}
}
