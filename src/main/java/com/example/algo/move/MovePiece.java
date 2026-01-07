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
	
	/*
	 * public function 
	 */
	public void execute(GameState state) {
		piece.moveTo(targetIndex);
		state.getCell(targetIndex).onLand(piece, state);
	}
}
