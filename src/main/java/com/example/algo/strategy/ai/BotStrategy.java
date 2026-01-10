package com.example.algo.strategy.ai;

import com.example.algo.move.*;
import com.example.algo.player.Player;
import com.example.algo.state.GameState;
import com.example.algo.strategy.MoveStrategy;
/*
 * DEV_NOTE: I added a package for each one to prevent deleting code 
 * 			 when I do it like this we can test multipul algos at the 
 * 			 same time without deleting the old ones 
 */
public class BotStrategy implements MoveStrategy{
	public MovePiece chooseMove(GameState state , Player player, int stick) {
		// same as the humane strategy , this throw is to prevent errors 
		// and here we would add the algorithm and then call the MovePiece function .
		throw new UnsupportedOperationException();
	}
}
