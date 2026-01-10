package com.example.algo.rules;

import com.example.algo.move.*;
import com.example.algo.player.Player;
import com.example.algo.state.*;
import com.example.algo.strategy.MoveStrategy;

/*
 * DEV_NOTES: the returns here are wrong , just for naming . 
 */
public class RuleEngine {
	public static boolean isLegal(MovePiece move , GameState state) { 
    int piece_current_index = move.getPiece().getPosition();
    int piece_target_index = move.getTargetIndex(); 
    //TODO:
    //1- if the current piece will be replaced with a piece from the same owner => return false. 
    //2- if piece_current_index is < 25 and piece_target_index is > 25 => false 
    //I don't this there is any other illegal moves ( there are moves that are bad for us , but that doesn't mean thery're illegal ! ).
    Piece target = state.getPieceAtIndex(piece_target_index);
    Piece current = move.getPiece();
    if(target != null){
      if(target.getOwner().equals(current.getOwner())) {
        return false;
      }else {return true;}
    }else if (piece_current_index < 25 && piece_target_index > 25 ){
      return false;
    }
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
