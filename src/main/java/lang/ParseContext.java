package lang;

@SuppressWarnings("rawtypes")
public abstract class ParseContext {
	private IOContext ioCtx;            // 入出力コンテキスト
	private Tokenizer tknz;            // 字句切り出しクラス
	// エラーの扱いに関するもの
	private int warningNo;    // 解析警告数
	private int errorNo;    // 解析エラー数

	// 入出力に関わるメソッド群
	public ParseContext(IOContext ioCtx, Tokenizer tknz) {
		this.ioCtx = ioCtx;
		this.tknz = tknz;
	}

	public IOContext getIOContext() {
		return ioCtx;
	}

	public void setIOContext(IOContext ioCtx) {
		this.ioCtx = ioCtx;
	}

	public Tokenizer getTokenizer() {
		return tknz;
	}

	public void setTokenizer(Tokenizer tknz) {
		this.tknz = tknz;
	}

	public void errorReport() {
		String errstr, warnstr;
		if (errorNo > 0) {
			errstr = "%%% 問題箇所が全部で" + errorNo + "件ありました。";
		} else {
			errstr = "%%% 問題箇所はありません。";
		}
		warnstr = (warningNo > 0) ? ("その他に警告は" + warningNo + "件ありました。") : "";
		ioCtx.getErrStream().println(errstr + warnstr);
	}

	private void message(final String s) {
		ioCtx.getErrStream().println(s);
	}

	// エラー（処理系が処理しきれない誤り）
	public boolean hasNoError() {
		return errorNo == 0;
	}

	public void error(final String s) {
		message(s);
		++errorNo;
	}

	// 本当に致命的な場合は例外を投げる
	public void fatalError(final String s) throws FatalErrorException {
		fatalError(s, null);
	}

	public void fatalError(final String err, String hint) throws FatalErrorException {
		var err_colored = "\033[31m" + err + "\033[m";
		if (hint != null) {
			var hint_colored = "\033[33m(hint: " + hint + ")\033[m";
			error(err_colored + '\n' + hint_colored);
		} else {
			error(err_colored);
		}
		throw new FatalErrorException();
	}

	// 警告（回復できる些細な誤り）
	public void warning(final String s) {
		message(s);
		++warningNo;
	}
}