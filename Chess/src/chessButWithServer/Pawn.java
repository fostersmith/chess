package chessButWithServer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Pawn extends ChessPiece {

	public static final char PAWN_CODE = 'P';
	private boolean firstMove = true;

	public static BufferedImage W_PAWN;
	public static BufferedImage B_PAWN;

	public Pawn(boolean color) {
		super(color,PAWN_CODE,generateImages()[color?0:1]);
	}
	
	public Pawn(int x, int y, boolean color) {
		super(x, y, color,PAWN_CODE,generateImages()[color?0:1]);
	}

	@Override
	public boolean canMoveTo(int x, int y, ChessManager game) {
		if(x>=ChessManager.BOARD_WIDTH||y>=ChessManager.BOARD_HEIGHT||x<0||y<0)
			return false;
		int yMovementPossible = color?1:-1;
		if((x==this.x-1||x==this.x+1)&&y==this.y+yMovementPossible)
			if(game.getPieceAt(x,y)!=null)
				return game.getPieceAt(x, y).color()!=color;
		if(x==this.x&&y==this.y+yMovementPossible)
			return game.getPieceAt(x, y) == null;
		else if(firstMove&&x==this.x&&y==this.y+yMovementPossible*2&&game.getPieceAt(this.x,this.y+yMovementPossible)==null)
			return game.getPieceAt(x, y) == null;
		return false;
	}
	
	@Override
	public void setPosition(int x, int y) {
		super.setPosition(x, y);		
	}

	public void setFirstMove(boolean firstMove) {
		this.firstMove = firstMove;
	}
	
	private final static BufferedImage[] generateImages() {
		try {
			W_PAWN = ImageIO.read(new File("chess_pieces\\Chess_plt60.png"));
			B_PAWN = ImageIO.read(new File("chess_pieces\\Chess_pdt60.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedImage[] {W_PAWN,B_PAWN};
	}

}
