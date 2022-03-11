package chessButWithServer;

import java.awt.image.BufferedImage;

public abstract class ChessPiece {
	protected int x, y;
	protected char code;
	protected BufferedImage im;
	/**
	 * true - white, false - black
	 */
	protected final boolean color;
	
	public ChessPiece(int x, int y, boolean color, char code, BufferedImage img) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.code = code;
		im = img;
	}
	
	public ChessPiece(boolean color, char code, BufferedImage img) {
		this.color = color;
		this.code = code;
		im = img;
	}
	
	public abstract boolean canMoveTo(int x, int y, ChessManager game);
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public final void setImage(BufferedImage img) {
		im = img;
	}
	
	public final int x() {
		return x;
	}
	public final int y() {
		return y;
	}
	public final boolean color() {
		return color;
	}
	public final char getCode() {
		return code;
	}
	public final BufferedImage getImage() {
		return im;
	}
}
