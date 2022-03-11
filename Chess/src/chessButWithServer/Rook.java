package chessButWithServer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Rook extends ChessPiece {

	public static final char ROOK_CODE = 'R';

	public static BufferedImage W_ROOK;
	public static BufferedImage B_ROOK;

	public Rook(boolean color) {
		super(color,ROOK_CODE,generateImages()[color?0:1]);
	}
	
	public Rook(int x, int y, boolean color) {
		super(x, y, color,ROOK_CODE,generateImages()[color?0:1]);
	}

	@Override
	public boolean canMoveTo(int x, int y, ChessManager game) {
		if(x>=ChessManager.BOARD_WIDTH||y>=ChessManager.BOARD_HEIGHT||x<0||y<0)
			return false;
		if(game.getPieceAt(x, y)!=null)
			if(game.getPieceAt(x, y).color()==color)
				return false;
		if(x==this.x) {
			if(y>this.y) {
				for(y-=1;y>this.y;--y)
					if(game.getPieceAt(x, y)!=null)
						return false;
				return true;
			} else if(y<this.y) {
				for(y+=1;y<this.y;++y)
					if(game.getPieceAt(x, y)!=null)
						return false;
				return true;
			}
		} else if(y==this.y) {
			if(x>this.x) {
				for(x-=1;x>this.x;--x)
					if(game.getPieceAt(x, y)!=null)
						return false;
				return true;
			} else if(x<this.x) {
				for(x+=1;x<this.x;++x)
					if(game.getPieceAt(x, y)!=null)
						return false;
				return true;
			}			
		}
		return false;
	}

	private final static BufferedImage[] generateImages() {
		try {
			W_ROOK = ImageIO.read(new File("chess_pieces\\Chess_rlt60.png"));
			B_ROOK = ImageIO.read(new File("chess_pieces\\Chess_rdt60.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedImage[] {W_ROOK,B_ROOK};
	}

}
