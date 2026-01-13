package com.example.algo.rules;

import com.example.algo.move.*;
import com.example.algo.player.Player;
import com.example.algo.state.*;
import com.example.algo.strategy.MoveStrategy;

/*
 * DEV_NOTES: the returns here are wrong , just for naming . 
 */
public class RuleEngine {
  public static boolean isLegal(MovePiece move, GameState state) {
    int piece_current_index = move.getPiece().getPosition();
    int piece_target_index = move.getTargetIndex();

    // Rule 1: Cannot move to same cell
    if (piece_current_index == piece_target_index) {
      return false;
    }

    // Rule 2: Exiting the board (targetIndex > 30) is only legal if piece is in
    // last 5 cells (26-30)
    if (piece_target_index > 30) {
      // Allow exit only if piece is in cells 26-30
      return (piece_current_index >= 26 && piece_current_index <= 30);
    }

    // Rule 3: Cannot jump from before cell 25 to after cell 25 (must land exactly
    // on 26)
    if (piece_current_index < 25 && piece_target_index > 25) {
      return false;
    }

    // Rule 4: If target cell has a piece, check ownership
    if (piece_target_index < state.board.length) {
      Piece target = state.getPieceAtIndex(piece_target_index);
      if (target != null) {
        // Cannot swap with own piece
        if (target.getOwner().equals(move.getPiece().getOwner())) {
          return false;
        }
        // Can swap with opponent piece
        return true;
      }
    }

    // Rule 5: All other moves are legal
    return true;
  }

  public MovePiece resolveMove(Player player,
      MoveStrategy strategy,
      GameState state,
      int stick) {
    MovePiece move;
    do {
      move = strategy.chooseMove(state, player, stick);
    } while (!isLegal(move, state));
    return move;
  }
}
