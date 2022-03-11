package chessButWithServer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Knight extends ChessPiece {

	public static final char KNIGHT_CODE = 'N';

	public static BufferedImage W_KNIGHT;
	public static BufferedImage B_KNIGHT;

	public Knight(boolean color) {
		super(color,KNIGHT_CODE,generateImages()[color?0:1]);
	}
	
	public Knight(int x, int y, boolean color) {
		super(x, y, color,KNIGHT_CODE,generateImages()[color?0:1]);
	}

	@Override
	public boolean canMoveTo(int x, int y, ChessManager game) {
		if(x>=ChessManager.BOARD_WIDTH||y>=ChessManager.BOARD_HEIGHT||x<0||y<0)
			return false;
		if(game.getPieceAt(x, y)!=null)
			if(game.getPieceAt(x, y).color()==this.color)
				return false;
		return ((x==this.x+2||x==this.x-2)^(y==this.y+2||y==this.y-2))&&x!=this.x&&y!=this.y&&x<=this.x+2&&y<=this.y+2&&x>=this.x-2&&y>=this.y-2; // 	:|
	}
	
	private final static BufferedImage[] generateImages() {
		try {
			W_KNIGHT = ImageIO.read(new File("chess_pieces\\Chess_nlt60.png"));
			B_KNIGHT = ImageIO.read(new File("chess_pieces\\Chess_ndt60.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedImage[] {W_KNIGHT,B_KNIGHT};
	}

	
}
/*
                                                             __________
                                                            /          \
                                                           |            |  
	  ___                                                   \__________/    
	 /. .\                                                    \\ || //
	(  >  )                                                    \\||//
	 \_-_/                        _______                       |  |
	 __|__              'woof'   /       \                      |  | 
	   |            /\__ /     [|_________|]                    |  |
	   |     \ ____/ ^__]       |O  ___  O|                     |  |
	  / \     | ____ |          | _|___|_ |                     |  |
____ /   \ __ ||    || ______ __||_______||________________ ___ |  | ______
*/
