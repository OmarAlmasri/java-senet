package com.example.algo.strategy;

import com.example.algo.move.Move;
import com.example.algo.player.Player;
import com.example.algo.state.GameState;

public interface MoveStrategy {
	Move chooseMove(GameState state, Player player);
}
