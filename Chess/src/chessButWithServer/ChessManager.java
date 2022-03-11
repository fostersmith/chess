package chessButWithServer;

public class ChessManager {
	public static final int BOARD_WIDTH = 8, BOARD_HEIGHT= 8;
	private ChessPiece[][] pieces;
	private int whiteScore, blackScore;
	private boolean colorPlaying;
	private boolean winner;
	private King kl, kd;
	
	public ChessManager() {
		reset();
	}
	
	public boolean isWon() {
		
		if(getPieceAt(kl.x(),kl.y())!=kl||getPieceAt(kd.x(),kd.y())!=kd) {
			winner = getPieceAt(kl.x(),kl.y())==kl;
			return true;
		}
		
		boolean canMove = false;
		
		for(int r = 0; r < pieces.length; ++r)
			for(int c = 0; c < pieces.length; ++c)
				if(pieces[r][c]!=null)
					if(pieces[r][c].color() == colorPlaying) {
						for(int r1 = 0; r1 < pieces.length; ++r1)
							for(int c1 = 0; c1 < pieces[r1].length; ++c1) {
								boolean canMoveTo = pieces[r][c].canMoveTo(c1, r1, this);
								if(canMoveTo) {
									ChessPiece temp = getPieceAt(c1,r1);
									int whiteOrigScore = whiteScore, blackOrigScore = blackScore;
									movePiece(c,r,c1,r1,false);
									canMove = canMove || !isChecked(colorPlaying);
									movePiece(c1,r1,c,r,false);
									setPieceAt(c1,r1,temp);
									whiteScore = whiteOrigScore;
									blackScore = blackOrigScore;
								}
							}
					}
		winner = !colorPlaying;
		return !canMove;
	}
	
	public boolean getWinner() {
		return winner; 
	}
	
	public int getWhiteScore() {
		return whiteScore;
	}
	
	public int getBlackScore() {
		return blackScore;
	}

	public void movePiece(int x1, int y1, int x2, int y2, boolean permanent) {
		//animateMovement(x1,y1,x2,y2);
		if(getPieceAt(x2,y2)!=null) {
			if(getPieceAt(x2,y2).color())
				++blackScore;
			else
				++whiteScore;
		}
		if(permanent&&getPieceAt(x1,y1).getClass()==Pawn.class)
			((Pawn)getPieceAt(x1,y1)).setFirstMove(false);
		ChessPiece temp = getPieceAt(x1, y1);
		setPieceAt(x1,y1,null);
		setPieceAt(x2,y2,temp);
	}
	
	public ChessPiece getPieceAt(int x, int y) {
		return pieces[y][x];
	}		
	
	public void setPieceAt(int x, int y, ChessPiece newPiece) {
		if(newPiece!=null)
			newPiece.setPosition(x, y);
		pieces[y][x] = newPiece;
	}

	public boolean isChecked(boolean color) {
		boolean checked = false;
		for(int r = 0; r < pieces.length&&!checked; ++r)
			for(int c = 0; c < pieces.length && !checked; ++c)
				if(pieces[r][c]!=null)
					if(pieces[r][c].color()!=color)
						checked = pieces[r][c].canMoveTo((color?kl:kd).x(),(color?kl:kd).y(), this);
		return checked;
	}

	public void reset() {
		kl = new King(true);
		kd = new King(false);
		whiteScore = 0;
		blackScore = 0;
		colorPlaying = true;
		pieces = new ChessPiece[BOARD_HEIGHT][BOARD_WIDTH];
		pieces[0] = new ChessPiece[] {new Rook(true), new Knight(true), new Bishop(true), new Queen(true), kl, new Bishop(true), new Knight(true), new Rook(true)};
		for(int i = 0; i < BOARD_WIDTH; ++i) {
			pieces[BOARD_HEIGHT-2][i] = new Pawn(false);
			pieces[1][i] = new Pawn(true);
		}
		pieces[BOARD_HEIGHT-1] = new ChessPiece[] {new Rook(false), new Knight(false), new Bishop(false), new Queen(false), kd, new Bishop(false), new Knight(false), new Rook(false)};		
		for(int r = 0; r < pieces.length; ++r)
			for(int c = 0; c < pieces[0].length; ++c)
				if(pieces[r][c]!=null)
					pieces[r][c].setPosition(c, r);
	}
	
	public ChessPiece[][] getPieces() {
		return pieces;
	}
}
