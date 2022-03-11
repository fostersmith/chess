package chessButWithServer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Queen extends ChessPiece {

	public static final char QUEEN_CODE = 'Q';

	public static BufferedImage W_QUEEN;
	public static BufferedImage B_QUEEN;

	public Queen(boolean color) {
		super(color,QUEEN_CODE,generateImages()[color?0:1]);
	}
	
	public Queen(int x, int y, boolean color) {
		super(x, y, color,QUEEN_CODE,generateImages()[color?0:1]);
	}

	@Override
	public boolean canMoveTo(int x, int y, ChessManager game) {
		if(x>=ChessManager.BOARD_WIDTH||y>=ChessManager.BOARD_HEIGHT||x<0||y<0)
			return false;
		return(new Bishop(this.x,this.y,color).canMoveTo(x, y, game)||new Rook(this.x,this.y,color).canMoveTo(x, y, game));
	}
	
	private final static BufferedImage[] generateImages() {
		try {
			W_QUEEN = ImageIO.read(new File("chess_pieces\\Chess_qlt60.png"));
			B_QUEEN = ImageIO.read(new File("chess_pieces\\Chess_qdt60.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedImage[] {W_QUEEN,B_QUEEN};
	}

}
