package com.example.algo;

import com.example.algo.move.Move;
import com.example.algo.player.Player;
import com.example.algo.rules.RuleEngine;
import com.example.algo.setup.GameInitializer;
import com.example.algo.state.GameState;
import com.example.algo.strategy.human.HumanStrategy;
import com.example.algo.util.GeneralUtil;

public class App 
{
    public static void main( String[] args )
    {
    	//TODO: 
    	//1- initialize the game (board , pieces , players ) . 
    	GameInitializer initializer = new GameInitializer();
    	GameState initialState = initializer.createNewGame();
    	
    	//2- create players and the strategies . 
    	//(in the first case both of them is a HumanStrategy ) .
    	Player yousuf = new Player("Yousuf",new HumanStrategy());
    	Player omar = new Player("omar",new HumanStrategy());
    	
    	Player[] players = { yousuf , omar };
    	int currentPlayerIndex =0; 
    	
    	boolean gameOver = false;
    	
    	RuleEngine rules = new RuleEngine();
    	// 3- create a game loop 
    	// this would be the most important function cause here everything would work. 
    	while(!gameOver) {
    		Player current = players[currentPlayerIndex];
    		// let the current player try a move . 
    		try {
    			/*
    			 * in here we would select the move and the apply the move 
    			 */
    			Move move = rules.resolveMove(current,
    										  current.getStrategy(), 
    										  initialState);
    			// we would apply the move , I think this is good .
    			// initialState.applyMove(move);
    		}catch(UnsupportedOperationException e) {
    			System.out.println(current.getName() + "move is wrong");
    		}
    		
    		// switch to the other player : 
    		currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    		
    		// check if the game is over .
    		gameOver = GeneralUtil.checkGameOver(initialState);
    	}
        System.out.println( "game over" );
    }
}
