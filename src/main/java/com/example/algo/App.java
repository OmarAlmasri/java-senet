package com.example.algo;

import java.util.List;
import java.util.Scanner;

import com.example.algo.move.*;
import com.example.algo.player.Player;
import com.example.algo.rules.RuleEngine;
import com.example.algo.setup.GameInitializer;
import com.example.algo.state.Cell;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;
import com.example.algo.strategy.ai.BotStrategy;
import com.example.algo.strategy.human.HumanStrategy;
import com.example.algo.util.GeneralUtil;
import com.example.algo.util.StickThrow;

public class App {
    public static void main(String[] args) {

        Player human = new Player("Player", new HumanStrategy());
        Player bot = new Player("Bot", new BotStrategy());

        System.out.println("Enable verbose algorithm output? (y/n)");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("y")) {
            BotStrategy.setVerboseMode(true);
        }


        Player[] players = { human, bot };

        // TODO:
        // 1- initialize the game (board , pieces , players ) .
        GameInitializer initializer = new GameInitializer();
        GameState state = initializer.createNewGame(players);
        // 2- create players and the strategies .
        // (in the first case both of them is a HumanStrategy ) .

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
            System.out.println("the current player is :" + current.getName());
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
            } else if (pending_atoum != null && state.getPendingAtoumPlayerIndex() == state.currentPlayerIndex) {
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
            } else if (pending_Horus != null && state.getPendingHorusPlayerIndex() == state.currentPlayerIndex) {
                System.out.println("Resolving horus effect! Stick rolled: " + stick);
                MovePiece chosenMove = current.getStrategy().chooseMove(state, current, stick);
                if (chosenMove.getPiece().equals(pending_Horus)) {
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

            } else {
                // Normal turn
                System.out.println(current.getName() + " rolled: " + stick);
                MovePiece move = current.getStrategy().chooseMove(state, current, stick);

                // Check if move is null (no legal moves available)
                if (move == null) {
                    System.out.println("  → No legal moves available, skipping turn");
                    state.switchPlayer();
                    continue;
                }

                // Get position before executing
                // Calculate oldPosition from targetPosition and stick value
                int targetPosition = move.getTargetIndex();
                int oldPosition = targetPosition - stick; // Reverse calculate: target - stick = origin

                // Validate that this is a real move
                if (oldPosition == targetPosition || oldPosition < 1) {
                    // If calculation fails, try to get from piece directly
                    oldPosition = move.getPiece().getPosition();
                    if (oldPosition == targetPosition) {
                        System.out.println("  → ERROR: Invalid move - piece at cell " + oldPosition +
                                " cannot move to same cell");
                        state.switchPlayer();
                        continue;
                    }
                }

                // Check if target cell has opponent piece (will cause swap)
                Piece targetPiece = state.getPieceAtIndex(targetPosition);
                boolean willSwap = (targetPiece != null && !targetPiece.getOwner().equals(current));

                // Execute the move
                move.execute(state);

                // Get actual position after execution (may differ if swap or special effects
                // occurred)
                int actualNewPosition = move.getPiece().getPosition();

                // Print the move
                if (willSwap && actualNewPosition == targetPosition) {
                    // Normal swap occurred
                    System.out.println("  → Moved piece from cell " + oldPosition + " to cell " + targetPosition +
                            " (swapped with opponent)");
                } else if (actualNewPosition != targetPosition) {
                    // Special effect changed the position
                    System.out.println("  → Moved piece from cell " + oldPosition + " to cell " + targetPosition +
                            " (ended at cell " + actualNewPosition + " due to special effect)");
                } else {
                    // Normal move
                    System.out.println("  → Moved piece from cell " + oldPosition + " to cell " + targetPosition);
                }
            }

            gameOver = GeneralUtil.checkGameOver(state);
            System.out.println("checked the game over ");
            state.switchPlayer();
        }

        GeneralUtil.printBoard(state);

        System.out.println("game over");
    }
}
