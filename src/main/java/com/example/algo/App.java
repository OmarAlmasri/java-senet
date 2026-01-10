package com.example.algo;

import java.util.List;

import com.example.algo.move.*;
import com.example.algo.player.Player;
import com.example.algo.rules.RuleEngine;
import com.example.algo.setup.GameInitializer;
import com.example.algo.state.Cell;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;
import com.example.algo.strategy.human.HumanStrategy;
import com.example.algo.util.GeneralUtil;
import com.example.algo.util.StickThrow;

public class App 
{
    public static void main( String[] args )
	{

		Player yousuf = new Player("Yousuf", new HumanStrategy());
		Player omar = new Player("omar", new HumanStrategy());

		Player[] players = { yousuf, omar };

		//TODO: 
		//1- initialize the game (board , pieces , players ) . 
		GameInitializer initializer = new GameInitializer();
		GameState state = initializer.createNewGame(players);
		//2- create players and the strategies . 
		//(in the first case both of them is a HumanStrategy ) .

		int currentPlayerIndex = 0;

		boolean gameOver = false;

		GeneralUtil.testGameInitialization(state);
		RuleEngine rules = new RuleEngine();
    System.out.println(state.board.toString());
    state.printCells();
		// 3- create a game loop 
		// this would be the most important function cause here everything would work. 
		while (!gameOver) {
            // Current player
            Player current = state.getCurrentPlayer();
            GeneralUtil.printBoard(state);
            int stick = StickThrow.throwSticks();

            /*
             * =================================================
             * Handling the last 3 cases effects 
             * =================================================
             */
            Piece pending = state.getPendingThreeTruthsPiece();
            Piece pending_atoum = state.getPendingAtoumPiece();
            Piece pending_Horus = state.getPendingHorusPiece();
            if (pending != null && state.getPendingThreeTruthsPlayerIndex() == state.currentPlayerIndex) {
                System.out.println("Resolving Three Truths effect! Stick rolled: " + stick);

                MovePiece chosenMove = current.getStrategy().chooseMove(state, current, stick);

                if (chosenMove.getPiece().equals(pending) && stick == 3) {
                    System.out.println(pending.getOwner().getName() + "'s piece is removed by Three Truths!");
                    state.removePiece(pending);
                    state.clearPendingThreeTruthsPiece();
            gameOver = GeneralUtil.checkGameOver(state);
            state.switchPlayer();
                    continue;
                } else {
                    System.out.println(pending.getOwner().getName() + "'s piece is sent to Rebirth!");
                    GeneralUtil.sendToReBirth(pending, state);
                    state.clearPendingThreeTruthsPiece();
            gameOver = GeneralUtil.checkGameOver(state);
            state.switchPlayer();
                    continue;
                }
            } else if (pending_atoum != null && state.getPendingAtoumPlayerIndex() == state.currentPlayerIndex){
                System.out.println("Resolving atoum effect! Stick rolled: " + stick);
                MovePiece chosenMove = current.getStrategy().chooseMove(state, current, stick);
                if (chosenMove.getPiece().equals(pending_atoum) && stick == 2) {
                    System.out.println(pending_atoum.getOwner().getName() + "'s piece is removed by Atoum!");
                    state.removePiece(pending_atoum);
                    state.clearPendingAtoumPiece();
            gameOver = GeneralUtil.checkGameOver(state);
            state.switchPlayer();
                    continue;
                } else {
                    System.out.println(pending_atoum.getOwner().getName() + "'s piece is sent to Rebirth!");
                    GeneralUtil.sendToReBirth(pending_atoum, state);
                    state.clearPendingAtoumPiece();
            gameOver = GeneralUtil.checkGameOver(state);
            state.switchPlayer();
                    continue;
                }
            } else if (pending_Horus != null && state.getPendingHorusPlayerIndex() == state.currentPlayerIndex){
                System.out.println("Resolving horus effect! Stick rolled: " + stick);
                MovePiece chosenMove = current.getStrategy().chooseMove(state, current, stick);
                if (chosenMove.getPiece().equals(pending_Horus) && stick == 2) {
                    System.out.println(pending_Horus.getOwner().getName() + "'s piece is removed by Horus!");
                    state.removePiece(pending_Horus);
                    state.clearPendingHorusPiece();
            gameOver = GeneralUtil.checkGameOver(state);
            state.switchPlayer();
                    continue;
                } else {
                    System.out.println(pending_Horus.getOwner().getName() + "'s piece is sent to Rebirth!");
                    GeneralUtil.sendToReBirth(pending_Horus, state);
                    state.clearPendingHorusPiece();
            gameOver = GeneralUtil.checkGameOver(state);
            state.switchPlayer();
                    continue;
                }

            }else {
                // Normal turn
                System.out.println(current.getName() + " rolled: " + stick);
                MovePiece move = current.getStrategy().chooseMove(state, current, stick);
                move.execute(state);
            }

            gameOver = GeneralUtil.checkGameOver(state);

            state.switchPlayer();
        }

		GeneralUtil.printBoard(state);
		
		System.out.println("game over");
	}
}
