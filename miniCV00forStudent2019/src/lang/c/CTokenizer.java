package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.*;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule	rule;
	private int			lineNo, colNo;
	private char		backCh;
	private boolean		backChExist = false;

	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1; colNo = 1;
	}

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n')  { colNo = 1; ++lineNo; }
		//System.err.print("'"+ch+"'("+(int)ch+")");
		return ch;
	}
	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') { --lineNo; }
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;
	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}
	// 次のトークンを読んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
//		System.out.println("Token='" + currentTk.toString());
		return currentTk;
	}

	enum State {
		INIT,
		EOF,
		ILL,
		DEC,
		PLUS,
		MINUS,
		SLASH,
		LCOM,
		BCOM,
		BCOM_MAYBE_END,
	}

	private CToken readToken() {
		CToken tk = null;
		char ch;
		int  startCol = colNo;
		StringBuilder text = new StringBuilder();

		State state = State.INIT;
		boolean accept = false;
		while (!accept) {
			System.err.println("Current State : " + state);
			switch (state) {
			case INIT:					// 初期状態
				ch = readChar();
				if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					/* 空白を読み飛ばす */
				} else if (ch == (char) -1) {	// EOF
					startCol = colNo - 1;
					state = State.EOF;
				} else if (ch >= '0' && ch <= '9') {
					startCol = colNo - 1;
					text.append(ch);
					state = State.DEC;
				} else if (ch == '+') {
					startCol = colNo - 1;
					text.append(ch);
					state = State.PLUS;
				} else if (ch == '-') {
					startCol = colNo - 1;
					text.append(ch);
					state = State.MINUS;
				} else if (ch == '/') {
					startCol = colNo - 1;
					text.append(ch);
					state = State.SLASH;
				} else {			// ヘンな文字を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = State.ILL;
				}
				break;
			case EOF:					// EOFを読んだ
				tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
				accept = true;
				break;
			case ILL:					// ヘンな文字を読んだ
				tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
				accept = true;
				break;
			case DEC:					// 数（10進数）の開始
				ch = readChar();
				if (Character.isDigit(ch)) {
					text.append(ch);
				} else {
					// 数の終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
					accept = true;
				}
				break;
			case PLUS:					// +を読んだ
				tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
				accept = true;
				break;
			case MINUS:
				tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
				accept = true;
				break;
			case SLASH:
				ch = readChar();
				switch (ch) {
					case '/': // ラインコメント
						state = State.LCOM;
						text.append(ch);
						break;
					case '*': // ブロックコメント
						state = State.BCOM;
						text.append(ch);
						break;
					default:
						backChar(ch);
						state = State.ILL;
						break;
				}
				break;
			case LCOM:
				ch = readChar();
				if (ch == '\n') {
					text.delete(0, text.length()); // コメントがtextに入っているので初期化
					state = State.INIT;
				} else if (ch == (char)-1) {
					text.delete(0, text.length()); // コメントがtextに入っているので初期化
					state = State.EOF;
				}
				break;
			case BCOM:
				ch = readChar();
				if (ch == '*') { // ブロックコメントの終わりかもしれない
					text.append(ch);
					state = State.BCOM_MAYBE_END;
				} else if (ch == (char)-1) { // 終わる前にEOFを踏んだ
					state = State.ILL;
				}
				break;
			case BCOM_MAYBE_END:
				ch = readChar();
				switch (ch) {
					case '/': // ブロックコメントの終わり
						text.delete(0, text.length()); // コメントがtextに入っているので初期化
						state = State.INIT;
						break;
					case (char)-1: // 終わる前にEOFを踏んだ
						state = State.ILL;
					default: // 終わりじゃなかった
						state = State.BCOM;
						// fall-through
					case '*': // 終わりじゃないけどまた疑わしい
						text.append(ch);
						break;
				}
				break;
			}
		}
		System.err.println("ACCEPTED");
		return tk;
	}
}