package com.example.algo.strategy.human;

import java.util.Scanner;

import com.example.algo.move.MovePiece;
import com.example.algo.player.Player;
import com.example.algo.state.*;
import com.example.algo.strategy.MoveStrategy;

public class HumanStrategy implements MoveStrategy {
	public MovePiece chooseMove(GameState state, Player player, int stick) {
		// TODO:
		// 1- throw a stick .
		// 2- take the user input for the piece he want's to move .
		// 3- check if there is a piece there , and if it's belongs to the player . if
		// no => continue .
		// 4- check if the move is legal . if not => continue and re-choose a move .
		// 5- if the move is legal => return the move .

		// Note: Scanner is not closed intentionally - closing it would close System.in
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		MovePiece move = null;
		boolean isLegal = false;

		while (!isLegal) {
			System.out.println("Human Turn: " + player.getName() + ", stick rolled: " + stick);
			System.out.print("Enter piece index to move (1-30) or 's' to skip turn: ");

			// Check if input is a string (for 's' to skip)
			if (input.hasNext()) {
				String userInput = input.next().trim().toLowerCase();

				// Check if user wants to skip turn
				if (userInput.equals("s")) {
					System.out.println("Turn skipped.");
					return null; // Return null to skip turn
				}

				// Try to parse as integer
				try {
					int index = Integer.parseInt(userInput);


					if (index < 0 || index >= state.board.length) {
						System.out.println("Index out of bounds! Enter a number between 1 and 30.");
						continue;
					}

					Piece piece = state.getPieceAtIndex(index);
					if (piece == null) {
						System.out.println("No piece at this cell!");
						continue;
					}
					if (!piece.getOwner().equals(player)) {
						System.out.println("This piece is not yours!");
						continue;
					}

					move = new MovePiece(piece, piece.getPosition() + stick);
					isLegal = com.example.algo.rules.RuleEngine.isLegal(move, state);
					if (!isLegal)
						System.out.println("Illegal move! Try again.");
				} catch (NumberFormatException e) {
					System.out.println("Invalid input! Enter a number (1-30) or 's' to skip.");
				}
			}
		}

		return move;
	}
}
