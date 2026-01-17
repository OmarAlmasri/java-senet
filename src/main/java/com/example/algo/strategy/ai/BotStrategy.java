package com.example.algo.strategy.ai;

import java.util.ArrayList;
import java.util.List;

import com.example.algo.move.*;
import com.example.algo.player.Player;
import com.example.algo.rules.RuleEngine;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;
import com.example.algo.strategy.MoveStrategy;

/*
 * DEV_NOTE: I added a package for each one to prevent deleting code 
 * 			 when I do it like this we can test multipul algos at the 
 * 			 same time without deleting the old ones 
 */
public class BotStrategy implements MoveStrategy {
	/*
	 * definitions
	 */
	private static final int MAX_DEPTH = 3; // we can change this later guys...

	// also these values can be used for tuning our stupid algo
	private static final int POSITION_WEIGHT = 10;
	private static final int OPONENT_PENALTY = 10;
	private static final int WIN_BONUS = 10000;
	private static final int SPECIAL_CELL_BONUS = 50;

	// شباب هاد التعديل يلي خلا البوت متوحش
	private static final int ADVANCED_POSITION_MULTIPLIER = 2; // Double weight for positions 20-30
	private static final int MID_POSITION_MULTIPLIER = 1; // Normal weight for positions 10-19
	private static final int EARLY_POSITION_MULTIPLIER = 1; // Normal weight for positions 1-9

	public MovePiece chooseMove(GameState state, Player player, int stick) {
		List<MovePiece> moves = generateMoves(state, player, stick);

		if (moves.isEmpty()) {
			return null;
		}

		if (moves.size() == 1) {
			return moves.get(0);
		}

		MovePiece bestMove = null;
		int bestValue = Integer.MIN_VALUE;

		for (MovePiece move : moves) {

			GameState nexState = state.clone();

			// Find the piece in the cloned state
			Piece clonedPiece = null;
			for (Piece p : nexState.pieces) {
				if (p.getOwner().equals(move.getPiece().getOwner()) &&
						p.getPosition() == move.getPiece().getPosition()) {
					clonedPiece = p;
					break;
				}
			}

			if (clonedPiece == null) {
				continue;
			}

			MovePiece clonedMove = new MovePiece(clonedPiece, move.getTargetIndex());
			clonedMove.execute(nexState);
			nexState.switchPlayer();

			int value = expectiminimax(nexState, MAX_DEPTH - 1, player, false);

			if (value > bestValue) {
				bestValue = value;
				bestMove = move;
			}
		}

		return bestMove != null ? bestMove : moves.get(0);
	}

	private int expectiminimax(GameState state, int depth, Player maximizingPlayer, boolean isMaxNode) {
		if (depth == 0 || isTerminal(state)) {
			return evaluate(state, maximizingPlayer);
		}

		if (isMaxNode) {
			return maxValue(state, depth, maximizingPlayer);
		} else {
			return minValue(state, depth, maximizingPlayer);
		}
	}

	private int minValue(GameState state, int depth, Player maximizingPlayer) {
		return chanceValue(state, depth, maximizingPlayer, false);
	}

	private int maxValue(GameState state, int depth, Player maximizingPlayer) {
		return chanceValue(state, depth, maximizingPlayer, true);
	}

	private int chanceValue(GameState state, int depth, Player maximizingPlayer, boolean isOurTurn) {
		double expectedValue = 0.0;

		// Probabilities for stick throws
		double[] probabilities = {
				0.25, // 1: 4/16
				0.375, // 2: 6/16
				0.25, // 3: 4/16
				0.0625, // 4: 1/16
				0.0625 // 5: 1/16
		};

		int[] stickValues = { 1, 2, 3, 4, 5 };

		for (int i = 0; i < stickValues.length; i++) {
			int stickThrow = stickValues[i];
			double probability = probabilities[i];

			Player currentPlayer = isOurTurn ? maximizingPlayer : getOpponent(state, maximizingPlayer);

			List<MovePiece> moves = generateMoves(state, currentPlayer, stickThrow);

			if (moves.isEmpty()) {
				GameState nextState = state.clone();
				nextState.switchPlayer();
				int value = expectiminimax(nextState, depth - 1, maximizingPlayer, !isOurTurn);
				expectedValue += probability * value;
			} else {
				int bestValue;

				if (isOurTurn) {
					// Maximizing player
					bestValue = Integer.MIN_VALUE;
					for (MovePiece move : moves) {
						GameState nextState = state.clone();

						Piece clonedPiece = findPieceInState(nextState, move.getPiece());

						if (clonedPiece != null) {
							MovePiece clonedMove = new MovePiece(clonedPiece, move.getTargetIndex());
							clonedMove.execute(nextState);
							nextState.switchPlayer();

							int value = expectiminimax(nextState, depth - 1, maximizingPlayer, false);
							bestValue = Math.max(bestValue, value);
						}
					}
				} else {
					// Minimizing player
					bestValue = Integer.MAX_VALUE;
					for (MovePiece move : moves) {
						GameState nextState = state.clone();

						Piece clonedPiece = findPieceInState(nextState, move.getPiece());

						if (clonedPiece != null) {
							MovePiece clonedMove = new MovePiece(clonedPiece, move.getTargetIndex());
							clonedMove.execute(nextState);
							nextState.switchPlayer();

							int value = expectiminimax(nextState, depth - 1, maximizingPlayer, true);
							bestValue = Math.min(bestValue, value);
						}
					}
				}

				expectedValue += probability * bestValue;
			}
		}

		return (int) expectedValue;
	}

	/**
	 * Helper method to find the corresponding piece in a cloned state
	 */
	private Piece findPieceInState(GameState state, Piece originalPiece) {
		for (Piece p : state.pieces) {
			if (p.getOwner().equals(originalPiece.getOwner()) &&
					p.getPosition() == originalPiece.getPosition()) {
				return p;
			}
		}
		return null;
	}

	private int evaluate(GameState state, Player maximizingPlayer) {
		int score = 0;
		Player opponent = getOpponent(state, maximizingPlayer);

		// Get pieces for both players
		List<Piece> myPieces = state.getPiecesFor(maximizingPlayer);
		List<Piece> opponentPieces = state.getPiecesFor(opponent);

		for (Piece piece : myPieces) {
			int position = piece.getPosition();
			if (position > 30) {
				score += WIN_BONUS;
			} else {
				int multiplier = 1;
				if (position >= 20 && position <= 30) {
					multiplier = ADVANCED_POSITION_MULTIPLIER;
				} else if (position >= 10 && position < 20) {
					multiplier = MID_POSITION_MULTIPLIER;
				} else {
					multiplier = EARLY_POSITION_MULTIPLIER;
				}
				score += position * POSITION_WEIGHT * multiplier;
			}
		}

		for (Piece piece : opponentPieces) {
			int position = piece.getPosition();
			if (position > 30) {
				score -= WIN_BONUS;
			} else {
				int multiplier = 1;
				if (position >= 20 && position <= 30) {
					multiplier = ADVANCED_POSITION_MULTIPLIER;
				} else if (position >= 10 && position < 20) {
					multiplier = MID_POSITION_MULTIPLIER;
				} else {
					multiplier = EARLY_POSITION_MULTIPLIER;
				}
				score -= position * POSITION_WEIGHT * multiplier;
			}
		}

		if (hasPieceOnCell(state, maximizingPlayer, 15)) {
			score += SPECIAL_CELL_BONUS;
		}
		if (hasPieceOnCell(state, opponent, 15)) {
			score -= SPECIAL_CELL_BONUS;
		}

		if (hasPieceOnCell(state, maximizingPlayer, 26)) {
			score += SPECIAL_CELL_BONUS;
		}
		if (hasPieceOnCell(state, opponent, 26)) {
			score -= SPECIAL_CELL_BONUS;
		}

		for (Piece piece : myPieces) {
			int position = piece.getPosition();
			if (position >= 26 && position <= 30) {
				int bonus = 20 + (position - 25) * 15; // 26=35, 27=50, 28=65, 29=80, 30=95
				score += bonus;
			}
		}
		for (Piece piece : opponentPieces) {
			int position = piece.getPosition();
			if (position >= 26 && position <= 30) {
				int penalty = 20 + (position - 25) * 15; // 26=35, 27=50, 28=65, 29=80, 30=95
				score -= penalty;
			}
		}

		for (Piece piece : myPieces) {
			int position = piece.getPosition();
			if (position < 10) {
				int penalty = position <= 3 ? 30 : (position <= 6 ? 20 : 10);
				score -= penalty;
			}
		}
		for (Piece piece : opponentPieces) {
			int position = piece.getPosition();
			if (position < 10) {
				int reward = position <= 3 ? 30 : (position <= 6 ? 20 : 10);
				score += reward;
			}
		}

		return score;
	}

	private boolean isTerminal(GameState state) {
		for (Player player : state.players) {
			List<Piece> pieces = state.getPiecesFor(player);
			boolean allRemoved = true;

			for (Piece piece : pieces) {
				if (piece.getPosition() <= 30) {
					allRemoved = false;
					break;
				}
			}

			if (allRemoved) {
				return true;
			}
		}
		return false;
	}

	private Player getOpponent(GameState state, Player player) {
		for (Player p : state.players) {
			if (!p.equals(player)) {
				return p;
			}
		}
		return null;
	}

	private boolean hasPieceOnCell(GameState state, Player player, int cellNumber) {
		List<Piece> pieces = state.getPiecesFor(player);
		for (Piece piece : pieces) {
			if (piece.getPosition() == cellNumber) {
				return true;
			}
		}
		return false;
	}

	private List<MovePiece> generateMoves(GameState state, Player player, int stickThrow) {
		List<MovePiece> moves = new ArrayList<>();
		List<Piece> playerPieces = state.getPiecesFor(player);

		for (Piece piece : playerPieces) {
			int currentPos = piece.getPosition();

			if (currentPos > 30) {
				continue;
			}

			int targetPos = currentPos + stickThrow;

			if (targetPos <= 30 || (currentPos >= 26 && currentPos <= 30)) {
				MovePiece move = new MovePiece(piece, targetPos);

				if (RuleEngine.isLegal(move, state)) {
					moves.add(move);
				}
			}
		}

		return moves;
	}

}
