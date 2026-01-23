package com.example.algo.strategy.human;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.algo.move.MovePiece;
import com.example.algo.player.Player;
import com.example.algo.state.*;
import com.example.algo.strategy.MoveStrategy;

public class HumanStrategy implements MoveStrategy {
	public MovePiece chooseMove(GameState state, Player player, int stick) {
		// First, check if there are any legal moves available
		List<MovePiece> legalMoves = generateLegalMoves(state, player, stick);
		
		if (legalMoves.isEmpty()) {
			System.out.println("No legal moves available - skipping turn");
			return null;
		}

		// Note: Scanner is not closed intentionally - closing it would close System.in
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		MovePiece move = null;
		boolean isLegal = false;

		while (!isLegal) {
			System.out.println("Human Turn: " + player.getName() + ", stick rolled: " + stick);
			System.out.print("Enter piece index to move (0-29): ");

			// Check if input is available
			if (input.hasNext()) {
				String userInput = input.next().trim().toLowerCase();

				// Check if user wants to skip turn (only allowed if no legal moves)
				if (userInput.equals("s")) {
					System.out.println("You cannot skip when legal moves are available! Please make a move.");
					continue;
				}

				// Try to parse as integer
				try {
					int index = Integer.parseInt(userInput);

					if (index < 0 || index >= state.board.length) {
						System.out.println("Index out of bounds! Enter a number between 0 and 29.");
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
					System.out.println("Invalid input! Enter a number (0-29).");
				}
			}
		}

		return move;
	}

	/**
	 * Generate all legal moves for a player given a stick value
	 */
	private List<MovePiece> generateLegalMoves(GameState state, Player player, int stickThrow) {
		List<MovePiece> moves = new ArrayList<>();
		List<Piece> playerPieces = state.getPiecesFor(player);

		for (Piece piece : playerPieces) {
			int currentPos = piece.getPosition();

			// Skip pieces that have already finished
			if (currentPos > 30) {
				continue;
			}

			int targetPos = currentPos + stickThrow;

			// Check if this move is possible (either within board or exiting from last 5 cells)
			if (targetPos <= 30 || (currentPos >= 26 && currentPos <= 30)) {
				MovePiece move = new MovePiece(piece, targetPos);

				if (com.example.algo.rules.RuleEngine.isLegal(move, state)) {
					moves.add(move);
				}
			}
		}

		return moves;
	}
}
