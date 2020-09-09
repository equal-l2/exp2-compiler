package lang.c;

import lang.Tokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	private final CTokenRule rule;
	private int lineNo, colNo;
	private char backCh;
	private boolean backChExist = false;

	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1;
		colNo = 1;
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
		if (ch == '\n') {
			colNo = 1;
			++lineNo;
		}
		//System.err.print("'"+ch+"'("+(int)ch+")");
		return ch;
	}

	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') {
			--lineNo;
		}
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
		PLUS,
		MINUS,
		AMP,
		SLASH,
		MULT,
		DIV,
		LPAR,
		RPAR,
		LCOM,
		BCOM,
		BCOM_MAYBE_END,
		NUM,
		OCT,
		HEX_BEFORE,
		HEX,
		DEC,
		LBRA,
		RBRA,
		IDENT,
	}

	private CToken readToken() {
		CToken tk = null;
		char ch;
		int startCol = colNo;
		StringBuilder text = new StringBuilder();

		State state = State.INIT;
		boolean accept = false;
		while (!accept) {
			System.err.println("Current State : " + state);
			switch (state) {
				case INIT:                    // 初期状態
					ch = readChar();
					if (Character.isWhitespace(ch)) {
						/* 空白を読み飛ばす */
					} else if (ch == (char) -1) {    // EOF
						startCol = colNo - 1;
						state = State.EOF;
					} else if (ch >= '1' && ch <= '9') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.DEC;
					} else if (ch == '0') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.NUM;
					} else if (ch == '+') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.PLUS;
					} else if (ch == '-') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.MINUS;
					} else if (ch == '&') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.AMP;
					} else if (ch == '/') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.SLASH;
					} else if (ch == '*') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.MULT;
					} else if (ch == '(') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.LPAR;
					} else if (ch == ')') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.RPAR;
					} else if (ch == '[') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.LBRA;
					} else if (ch == ']') {
						startCol = colNo - 1;
						text.append(ch);
						state = State.RBRA;
					} else if (Character.isLetter(ch) || ch == '_'){
						startCol = colNo - 1;
						text.append(ch);
						state = State.IDENT;
					} else {
						startCol = colNo - 1;
						text.append(ch);
						state = State.ILL;
					}
					break;
				case EOF:                    // EOFを読んだ
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case ILL:                    // ヘンな文字を読んだ
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				case PLUS:                    // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case MINUS:
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case AMP:
					tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
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
						default: // それ以外は除算と見なす
							backChar(ch);
							state = State.DIV;
							break;
					}
					break;
				case MULT:
					tk = new CToken(CToken.TK_MULT, lineNo, startCol, "*");
					accept = true;
					break;
				case DIV:
					tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
					accept = true;
					break;
				case LPAR:
					tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
					accept = true;
					break;
				case RPAR:
					tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
					accept = true;
					break;
				case LBRA:
					tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "[");
					accept = true;
					break;
				case RBRA:
					tk = new CToken(CToken.TK_RPAR, lineNo, startCol, "]");
					accept = true;
					break;
				case LCOM:
					ch = readChar();
					if (ch == '\n') {
						text.delete(0, text.length()); // コメントがtextに入っているので初期化
						state = State.INIT;
					} else if (ch == (char) -1) {
						text.delete(0, text.length()); // コメントがtextに入っているので初期化
						state = State.EOF;
					}
					break;
				case BCOM:
					ch = readChar();
					text.append(ch);
					if (ch == '*') { // ブロックコメントの終わりかもしれない
						state = State.BCOM_MAYBE_END;
					} else if (ch == (char) -1) { // 終わる前にEOFを踏んだ
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
						case (char) -1: // 終わる前にEOFを踏んだ
							state = State.ILL;
							break;
						default: // 終わりじゃなかった
							state = State.BCOM;
							// fall-through
						case '*': // 終わりじゃないけどまた疑わしい
							text.append(ch);
							break;
					}
					break;
				case NUM:
					ch = readChar();
					if (ch >= '0' && ch <= '9') {
						text.append(ch);
						state = State.OCT;
					} else if (ch == 'x' || ch == 'X') {
						text.append(ch);
						state = State.HEX_BEFORE;
					} else { // 0単体ならここで切る
						backChar(ch);
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, "0");
						accept = true;
					}
					break;
				case HEX_BEFORE:
					ch = readChar();
					text.append(ch);
					if (ch >= '0' && ch <= '9') {
						state = State.HEX;
					} else {
						char c = Character.toLowerCase(ch);
						if (c >= 'a' && c <= 'f') {
							state = State.HEX;
						} else {
							state = State.ILL;
						}
					}
					break;
				case OCT:
					ch = readChar();
					if (ch >= '0' && ch <= '7') {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch);    // 数を表さない文字は戻す（読まなかったことにする）
						int n = Integer.decode(text.toString());
						if (n > 0xFFFF) {
							state = State.ILL;
						} else {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case HEX:
					ch = readChar();
					char c = Character.toLowerCase(ch);
					if ((ch >= '0' && ch <= '9') || (c >= 'a' && c <= 'f')) {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch);    // 数を表さない文字は戻す（読まなかったことにする）
						int n = Integer.decode(text.toString());
						if (n > 0xFFFF) {
							state = State.ILL;
						} else {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case DEC:
					ch = readChar();
					if (ch >= '0' && ch <= '9') {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch);    // 数を表さない文字は戻す（読まなかったことにする）
						int n = Integer.decode(text.toString());
						if (n > 0xFFFF) {
							state = State.ILL;
						} else {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case IDENT:
					ch = readChar();
					if (Character.isLetterOrDigit(ch) || ch == '_') {
						text.append(ch);
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_IDENT, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
			}

		}
		System.err.println("ACCEPTED");
		return tk;
	}
}
