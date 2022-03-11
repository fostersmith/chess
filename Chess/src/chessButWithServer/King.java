package chessButWithServer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class King extends ChessPiece{

	public static final char KING_CODE = 'K';

	public static BufferedImage W_KING;
	public static BufferedImage B_KING;

	public King(boolean color) {
		super(color,KING_CODE,generateImages()[color?0:1]);
	}
	
	public King(int x, int y, boolean color) {
		super(x, y, color,KING_CODE,generateImages()[color?0:1]);
	}

	@Override
	public boolean canMoveTo(int x, int y, ChessManager game) {
		if(x>=ChessManager.BOARD_WIDTH||y>=ChessManager.BOARD_HEIGHT||x<0||y<0)
			return false;
		if((x>=this.x-1&&x<=this.x+1&&y>=this.y-1&&y<=this.y+1)&&!(x==this.x&&y==this.y))
			if(game.getPieceAt(x, y)!=null)
				return game.getPieceAt(x, y).color()!=color;
			else
				return true;
		return false;
	}
	
	private final static BufferedImage[] generateImages() {
		try {
			W_KING = ImageIO.read(new File("chess_pieces\\Chess_klt60.png"));
			B_KING = ImageIO.read(new File("chess_pieces\\Chess_kdt60.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedImage[] {W_KING,B_KING};
	}

}
