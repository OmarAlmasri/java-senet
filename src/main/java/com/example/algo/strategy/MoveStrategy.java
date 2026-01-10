package com.example.algo.strategy;

import com.example.algo.move.*;
import com.example.algo.player.Player;
import com.example.algo.state.GameState;

public interface MoveStrategy {
	MovePiece chooseMove(GameState state, Player player, int stick);
}
