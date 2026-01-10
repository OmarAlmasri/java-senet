package com.example.algo.move;

import com.example.algo.state.GameState;
import com.example.algo.state.Piece;

public class MovePiece implements Move{
	/*
	 * definations and constructor 
	 */
	private final Piece piece; 
	private final int targetIndex; 
	
	public MovePiece(Piece piece , int targetIndex) {
		this.piece = piece ; 
		this.targetIndex = targetIndex; 
	}
  public Piece getPiece(){
    return this.piece; 
  }
  public int getTargetIndex(){
    return this.targetIndex;
  }
	/*
	 * public function 
	 */
	public void execute(GameState state) {
    Piece target;
    if(targetIndex >= state.board.length) target = null ;
     target = state.getPieceAtIndex(targetIndex);
    if(target == null ){
		piece.moveTo(targetIndex);
    }else { // if there is a piece in that cell then swap them . 
      int current_index = piece.getPosition(); 
      piece.moveTo(targetIndex); 
      target.moveTo(current_index);
    }
		state.getCell(targetIndex).onLand(piece, state);
	}
}
