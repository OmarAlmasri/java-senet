package com.example.algo.strategy.ai;

import java.util.ArrayList;
import java.util.List;

import com.example.algo.move.*;
import com.example.algo.player.Player;
import com.example.algo.rules.RuleEngine;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;
import com.example.algo.strategy.MoveStrategy;

public class BotStrategy implements MoveStrategy {

	// ==================== CONSTANTS ====================
	private static final int MAX_DEPTH = 3;
	private static final int POSITION_WEIGHT = 10;
	private static final int OPONENT_PENALTY = 10;
	private static final int WIN_BONUS = 10000;
	private static final int SPECIAL_CELL_BONUS = 50;
	private static final int ADVANCED_POSITION_MULTIPLIER = 2;
	private static final int MID_POSITION_MULTIPLIER = 1;
	private static final int EARLY_POSITION_MULTIPLIER = 1;

	// ==================== VERBOSE MODE ====================
	private static boolean verboseMode = false;
	private int nodeCounter = 0;
	private int currentDepth = 0;

	/**
	 * Enable or disable verbose output
	 * Call this before starting the game
	 */
	public static void setVerboseMode(boolean verbose) {
		verboseMode = verbose;
		if (verbose) {
			System.out.println("╔═══════════════════════════════════════════════════╗");
			System.out.println("║     VERBOSE MODE ENABLED - Algorithm Tracing     ║");
			System.out.println("╚═══════════════════════════════════════════════════╝");
		}
	}

	/**
	 * Check if verbose mode is enabled
	 */
	public static boolean isVerboseMode() {
		return verboseMode;
	}

	// ==================== VERBOSE HELPERS ====================

	private void printSeparator() {
		if (!verboseMode)
			return;
		System.out.println("═══════════════════════════════════════════════════");
	}

	private void printHeader(String header) {
		if (!verboseMode)
			return;
		System.out.println("\n╔═══════════════════════════════════════════════════╗");
		System.out.println("║ " + centerText(header, 49) + " ║");
		System.out.println("╚═══════════════════════════════════════════════════╝");
	}

	private void printSubHeader(String text) {
		if (!verboseMode)
			return;
		System.out.println("\n┌─────────────────────────────────────────────────┐");
		System.out.println("│ " + text);
		System.out.println("└─────────────────────────────────────────────────┘");
	}

	private void printIndented(int depth, String text) {
		if (!verboseMode)
			return;
		String indent = "  ".repeat(depth);
		System.out.println(indent + text);
	}

	private void printNodeInfo(String nodeType, int depth, String info) {
		if (!verboseMode)
			return;
		nodeCounter++;
		String indent = "  ".repeat(currentDepth - depth);
		System.out.printf("%s[Node #%d] %s (Depth=%d) %s%n",
				indent, nodeCounter, nodeType, depth, info);
	}

	private String centerText(String text, int width) {
		int padding = (width - text.length()) / 2;
		return " ".repeat(Math.max(0, padding)) + text;
	}

	// ==================== MAIN ALGORITHM ====================

	public MovePiece chooseMove(GameState state, Player player, int stick) {
		// Reset counters for this move
		nodeCounter = 0;
		currentDepth = MAX_DEPTH;

		if (verboseMode) {
			printHeader("NEW MOVE DECISION");
			System.out.println("Player: " + player.getName());
			System.out.println("Stick Throw: " + stick);
			System.out.println("Max Depth: " + MAX_DEPTH);
			printSeparator();
		}

		List<MovePiece> moves = generateMoves(state, player, stick);

		if (moves.isEmpty()) {
			if (verboseMode) {
				System.out.println("No legal moves available - skipping turn");
			}
			return null;
		}

		if (moves.size() == 1) {
			if (verboseMode) {
				System.out.println("!> Only one legal move available:");
				printMoveInfo(moves.get(0), 0);
			}
			return moves.get(0);
		}

		if (verboseMode) {
			printSubHeader("Evaluating " + moves.size() + " possible moves:");
		}

		MovePiece bestMove = null;
		int bestValue = Integer.MIN_VALUE;
		int moveIndex = 0;

		for (MovePiece move : moves) {
			moveIndex++;

			if (verboseMode) {
				System.out.println("\n┌─ Move " + moveIndex + "/" + moves.size() + " ──────");
				printMoveInfo(move, 1);
			}

			GameState nexState = state.clone();

			Piece clonedPiece = null;
			for (Piece p : nexState.pieces) {
				if (p.getOwner().equals(move.getPiece().getOwner()) &&
						p.getPosition() == move.getPiece().getPosition()) {
					clonedPiece = p;
					break;
				}
			}

			if (clonedPiece == null) {
				if (verboseMode) {
					System.out.println(" Piece not found in cloned state - skipping");
				}
				continue;
			}

			MovePiece clonedMove = new MovePiece(clonedPiece, move.getTargetIndex());
			clonedMove.execute(nexState);
			nexState.switchPlayer();

			int value = expectiminimax(nexState, MAX_DEPTH - 1, player, false);

			if (verboseMode) {
				System.out.printf("Expected Value: %d%n", value);
				if (value > bestValue) {
					System.out.println("NEW BEST MOVE!");
				}
			}

			if (value > bestValue) {
				bestValue = value;
				bestMove = move;
			}
		}

		if (verboseMode) {
			printHeader("DECISION SUMMARY");
			System.out.println("Total Nodes Explored: " + nodeCounter);
			System.out.println("Best Move Value: " + bestValue);
			System.out.println("\n> CHOSEN MOVE:");
			printMoveInfo(bestMove, 0);
			printSeparator();
		}

		return bestMove != null ? bestMove : moves.get(0);
	}

	private int expectiminimax(GameState state, int depth, Player maximizingPlayer, boolean isMaxNode) {
		if (depth == 0 || isTerminal(state)) {
			int evalValue = evaluate(state, maximizingPlayer);

			if (verboseMode) {
				String nodeType = isTerminal(state) ? "TERMINAL" : "LEAF";
				String explanation = isTerminal(state)
						? String.format("Eval=%d (game over)", evalValue)
						: String.format("Eval=%d (position score)", evalValue);
				printNodeInfo(nodeType, depth, explanation);
			}

			return evalValue;
		}

		if (isMaxNode) {
			return maxValue(state, depth, maximizingPlayer);
		} else {
			return minValue(state, depth, maximizingPlayer);
		}
	}

	private int minValue(GameState state, int depth, Player maximizingPlayer) {
		if (verboseMode) {
			printNodeInfo("MIN", depth, "Opponent's turn");
		}
		return chanceValue(state, depth, maximizingPlayer, false);
	}

	private int maxValue(GameState state, int depth, Player maximizingPlayer) {
		if (verboseMode) {
			printNodeInfo("MAX", depth, "Computer's turn");
		}
		return chanceValue(state, depth, maximizingPlayer, true);
	}

	private int chanceValue(GameState state, int depth, Player maximizingPlayer, boolean isOurTurn) {
		double expectedValue = 0.0;

		double[] probabilities = {
				0.25, // 1: 4/16
				0.375, // 2: 6/16
				0.25, // 3: 4/16
				0.0625, // 4: 1/16
				0.0625 // 5: 1/16
		};

		int[] stickValues = { 1, 2, 3, 4, 5 };

		if (verboseMode) {
			printNodeInfo("CHANCE", depth,
					String.format("Expected value = Σ(p(stick) × best_value(stick)) | %s turn",
							isOurTurn ? "Computer's" : "Opponent's"));
		}

		for (int i = 0; i < stickValues.length; i++) {
			int stickThrow = stickValues[i];
			double probability = probabilities[i];

			Player currentPlayer = isOurTurn ? maximizingPlayer : getOpponent(state, maximizingPlayer);

			List<MovePiece> moves = generateMoves(state, currentPlayer, stickThrow);

			if (moves.isEmpty()) {
				GameState nextState = state.clone();
				nextState.switchPlayer();
				int value = expectiminimax(nextState, depth - 1, maximizingPlayer, !isOurTurn);

				if (verboseMode) {
					printIndented(MAX_DEPTH - depth + 1,
							String.format("  Stick=%d (p=%.4f): No moves → skip → value=%d",
									stickThrow, probability, value));
				}

				expectedValue += probability * value;
			} else {
				int bestValue;

				if (isOurTurn) {
					bestValue = Integer.MIN_VALUE;

					if (verboseMode) {
						printIndented(MAX_DEPTH - depth + 1,
								String.format("  Stick=%d (p=%.1f%%): %d moves → pick MAX value",
										stickThrow, probability * 100, moves.size()));
					}

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

					if (verboseMode) {
						printIndented(MAX_DEPTH - depth + 1,
								String.format("    → Best MAX value: %d", bestValue));
					}
				} else {
					bestValue = Integer.MAX_VALUE;

					if (verboseMode) {
						printIndented(MAX_DEPTH - depth + 1,
								String.format("  Stick=%d (p=%.1f%%): %d moves → pick MIN value",
										stickThrow, probability * 100, moves.size()));
					}

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

					if (verboseMode) {
						printIndented(MAX_DEPTH - depth + 1,
								String.format("    → Best MIN value: %d", bestValue));
					}
				}

				expectedValue += probability * bestValue;
			}
		}

		if (verboseMode) {
			// Show calculation breakdown for educational purposes
			printIndented(MAX_DEPTH - depth,
					String.format("    Expected value: %.2f → %d (weighted average of all stick outcomes)",
							expectedValue, (int) expectedValue));
		}

		return (int) expectedValue;
	}

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

		List<Piece> myPieces = state.getPiecesFor(maximizingPlayer);
		List<Piece> opponentPieces = state.getPiecesFor(opponent);

		// Position evaluation with multipliers
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

		// Special cells
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

		// Endgame bonus
		for (Piece piece : myPieces) {
			int position = piece.getPosition();
			if (position >= 26 && position <= 30) {
				int bonus = 20 + (position - 25) * 15;
				score += bonus;
			}
		}
		for (Piece piece : opponentPieces) {
			int position = piece.getPosition();
			if (position >= 26 && position <= 30) {
				int penalty = 20 + (position - 25) * 15;
				score -= penalty;
			}
		}

		// Early game penalty
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

	// ==================== VERBOSE UTILITY METHODS ====================

	private void printMoveInfo(MovePiece move, int indentLevel) {
		if (!verboseMode || move == null)
			return;

		String indent = "  ".repeat(indentLevel);
		Piece piece = move.getPiece();
		int from = piece.getPosition();
		int to = move.getTargetIndex();

		System.out.printf("%sPiece: %s | Position: %d → %d | Distance: %d%n",
				indent, piece.getOwner().getName(), from, to, (to - from));
	}

	/**
	 * Print detailed statistics about the current search
	 */
	public void printStatistics() {
		if (!verboseMode)
			return;

		printHeader("SEARCH STATISTICS");
		System.out.println("Total nodes explored: " + nodeCounter);
		System.out.println("Maximum depth: " + MAX_DEPTH);
		System.out.println("Branching factor (avg): ~35 (5 stick × 7 pieces)");
		System.out.println("Theoretical max nodes: " + Math.pow(35, MAX_DEPTH));
		printSeparator();
	}

	/**
	 * Print a guide explaining how to read the verbose output
	 * This helps students and teachers understand the algorithm trace
	 */
	public static void printVerboseOutputGuide() {
		System.out.println("\n" + "=".repeat(70));
		System.out.println("HOW TO READ THE VERBOSE ALGORITHM OUTPUT");
		System.out.println("=".repeat(70));
		System.out.println("\nUnderstanding the Node Types:");
		System.out.println("  • MAX node: Computer's turn - trying to MAXIMIZE score");
		System.out.println("  • MIN node: Opponent's turn - trying to MINIMIZE score");
		System.out.println("  • CHANCE node: Uncertain outcome - calculating EXPECTED VALUE");
		System.out.println("  • LEAF node: Terminal state (depth=0) - evaluating final position");

		System.out.println("\nUnderstanding the Format:");
		System.out.println("  [Node #N] TYPE (Depth=D) Description");
		System.out.println("    • Node #N: Sequential number of nodes explored");
		System.out.println("    • Depth: How many moves ahead we're looking (0 = current position)");
		System.out.println("    • Indentation: Shows the tree structure (deeper = more indented)");

		System.out.println("\nUnderstanding Chance Nodes:");
		System.out.println("  Stick=1 (p=0.2500): 3 moves (MAX)");
		System.out.println("    • Stick=1: Possible stick throw value");
		System.out.println("    • p=0.2500: Probability of this stick throw (25%)");
		System.out.println("    • 3 moves: Number of legal moves available");
		System.out.println("    • (MAX/MIN): Whether we're maximizing or minimizing");

		System.out.println("\nUnderstanding Evaluation:");
		System.out.println("  [Node #X] LEAF (Depth=0) Eval=370");
		System.out.println("    • Eval=370: Final score of this position");
		System.out.println("    • Higher = better for computer, lower = better for opponent");

		System.out.println("\nUnderstanding Expected Value:");
		System.out.println("    Expected value: 418.75 → 418");
		System.out.println("    • Calculated as: Σ(probability × best_value_for_that_stick)");
		System.out.println("    • Example: 0.25×370 + 0.375×390 + 0.25×470 + 0.0625×450 + 0.0625×550");
		System.out.println("    • Rounded to integer for final result");

		System.out.println("\nReading a Typical Trace:");
		System.out.println("  1. MAX node: Computer considers its move");
		System.out.println("  2. CHANCE node: For each possible stick throw (1-5):");
		System.out.println("     a. Generate all legal moves for that stick");
		System.out.println("     b. For each move, evaluate recursively (go deeper)");
		System.out.println("     c. Pick best move (MAX) or worst for opponent (MIN)");
		System.out.println("     d. Weight by probability");
		System.out.println("  3. Sum all weighted values = Expected Value");
		System.out.println("  4. Choose move with highest expected value");

		System.out.println("\nKey Insights:");
		System.out.println("  • The algorithm looks 3 moves ahead (MAX_DEPTH=3)");
		System.out.println("  • It considers ALL possible stick throws (probabilistic)");
		System.out.println("  • Each position is evaluated using the evaluation function");
		System.out.println("  • The final decision is the move with best expected outcome");

		System.out.println("\n" + "=".repeat(70) + "\n");
	}
}