package com.example.algo.move;

import com.example.algo.state.Cell;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;

public class MovePiece implements Move {
	/*
	 * definations and constructor
	 */
	private final Piece piece;
	private final int targetIndex;

	public MovePiece(Piece piece, int targetIndex) {
		this.piece = piece;
		this.targetIndex = targetIndex;
	}

	public Piece getPiece() {
		return this.piece;
	}

	public int getTargetIndex() {
		return this.targetIndex;
	}

	/*
	 * public function
	 */
	public void execute(GameState state) {
		// Check if target is within board bounds (0-29)
		Piece target = null;
		if (targetIndex < state.board.length) {
			target = state.getPieceAtIndex(targetIndex);
		}

		// If target cell is empty or out of bounds, just move the piece
		if (target == null) {
			piece.moveTo(targetIndex);
		} else {
			// If there is a piece in that cell, swap them
			int current_index = piece.getPosition();
			piece.moveTo(targetIndex);
			target.moveTo(current_index);
		}

		// Only call onLand if the piece is still on the board (targetIndex < 30)
		// If targetIndex >= 30, the piece has exited the board - DO NOT call onLand
		if (targetIndex < state.board.length) {
			Cell targetCell = state.getCell(targetIndex);
			if (targetCell != null) {
				targetCell.onLand(piece, state);
			}
		} else {
      System.out.println("removed a piece for getting out of bounds");
      state.removePiece(piece);
    }
		// If targetIndex >= 30, piece has exited - no need to call onLand
	}
}
