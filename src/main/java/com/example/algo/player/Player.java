package com.example.algo.player;

import com.example.algo.move.Move;
import com.example.algo.state.GameState;
import com.example.algo.strategy.MoveStrategy;

public class Player {
	/*
	 * definations 
	 */
	private final String name; 
	private final MoveStrategy strategy; 

	/*
	 * constructors 
	 */
	public Player(String name , MoveStrategy strategy) {
		this.name = name ; 
		this.strategy = strategy;
	}
	/*
	 * public functions 
	 */
	public Move play(GameState state) {
		return strategy.chooseMove(state,this);
	}
	
	public String getName() {
		return this.name;
	}
	
	public MoveStrategy getStrategy() {
		return this.strategy;
	}
}
