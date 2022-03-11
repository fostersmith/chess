package chessButWithServer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bishop extends ChessPiece {
	
	public static final char BISHOP_CODE = 'B';
	public static BufferedImage W_BISHOP;
	public static BufferedImage B_BISHOP;

	public Bishop(boolean color) {
		super(color,BISHOP_CODE,generateImages()[color?0:1]);
	}
	
	public Bishop(int x, int y, boolean color) {
		super(x, y, color,BISHOP_CODE,generateImages()[color?0:1]);
	}

	@Override
	public boolean canMoveTo(int x, int y, ChessManager game) {
		if(x>=ChessManager.BOARD_WIDTH||y>=ChessManager.BOARD_HEIGHT||x<0||y<0)
			return false;
		if(game.getPieceAt(x,y)!=null)
			if(game.getPieceAt(x, y).color()==this.color)
				return false;
		
		if(y==(x-this.x)+this.y) {
			if(x>this.x) {
				for(x--,y--;x>this.x;x--,y--)
					if(game.getPieceAt(x, y)!=null)
						return false;
				return true;
			} else if(x<this.x) {
				for(x++,y++;x<this.x;x++,y++)
					if(game.getPieceAt(x, y)!=null)
						return false;
				return true;
			}
		} else if(y==-(x-this.x)+this.y) {
			if(x>this.x) {
				for(x--,y++;x>this.x;x--,y++)
					if(game.getPieceAt(x, y)!=null)
						return false;
				return true;
			} else if(x<this.x) {
				for(x++,y--;x<this.x;x++,y--)
					if(game.getPieceAt(x, y)!=null)
						return false;
				return true;
			}
		}
		return false;
	}
	
	private final static BufferedImage[] generateImages() {
		try {
			W_BISHOP = ImageIO.read(new File("chess_pieces\\Chess_blt60.png"));
			B_BISHOP = ImageIO.read(new File("chess_pieces\\Chess_bdt60.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedImage[] {W_BISHOP,B_BISHOP};
	}
}
