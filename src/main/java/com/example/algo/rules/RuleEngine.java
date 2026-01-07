package com.example.algo.rules;

import com.example.algo.move.Move;
import com.example.algo.player.Player;
import com.example.algo.state.GameState;
import com.example.algo.strategy.MoveStrategy;

/*
 * DEV_NOTES: the returns here are wrong , just for naming . 
 */
public class RuleEngine {
	public boolean isLegal(Move move , GameState state) {
		return true;
	}
	
	public Move resolveMove(Player player,
							MoveStrategy strategy, 
							GameState state) {
		Move move ; 
		do {
			move = strategy.chooseMove(state, player);
		}while(!isLegal(move ,state));
		
		return move; 
	}
}
