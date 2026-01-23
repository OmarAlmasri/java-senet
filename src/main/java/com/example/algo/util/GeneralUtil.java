package com.example.algo.util;

import java.util.*;

import com.example.algo.player.Player;
import com.example.algo.state.Cell;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;
import com.example.algo.state.SpecialCell;

public class GeneralUtil {

	// Coloring for Terminal
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";

	/**
	 * Helper method to get color for a player
	 */
	private static String getPlayerColor(Player player, Player[] players) {
		if (player == null)
			return RESET;
		for (int i = 0; i < players.length; i++) {
			if (players[i].equals(player)) {
				// First player gets GREEN, second gets RED
				return (i == 0) ? GREEN : RED;
			}
		}
		return RESET;
	}

	public static void printBoard(GameState state) {
		System.out.println(
				"================================================================================================");
		System.out.println(
				"============================================SENET===============================================");
		System.out.println(
				"================================================================================================");
		int rows = 3;
		int cols = 10;

		// First, find max cell content length for alignment
		int maxLen = 0;
		for (int i = 0; i < state.board.length; i++) {
			final int idx = i;
			Cell cell = state.board[idx];
			Piece piece = state.pieces.stream()
					.filter(p -> p.getPosition() == idx).findFirst()
					.orElse(null);

			String content;
			if (piece != null) {
				content = piece.getOwner().getName();
			} else if (cell instanceof SpecialCell) {
				switch (i) {
					case 14:
						content = "(Rebirth)";
						break;
					case 25:
						content = "(Happiness)";
						break;
					case 26:
						content = "(water)";
						break;
					case 27:
						content = "(3Truthes)";
						break;
					case 28:
						content = "(re-Atoume)";
						break;
					case 29:
						content = "(Horus)";
						break;
					default:
						content = "(Special)";
						break;
				}
			} else {
				content = "(Empty)";
			}

			if (content.length() > maxLen)
				maxLen = content.length();
		}

		for (int row = 0; row < rows; row++) {
			for (int col = (row == 1 ? cols - 1 : 0); (row == 1 ? col >= 0 : col < cols); col += (row == 1 ? -1 : 1)) {
				int index = row * cols + col;
				Cell cell = state.board[index];
				Piece piece = state.pieces.stream()
						.filter(p -> p.getPosition() == index).findFirst()
						.orElse(null);

				String content;
				String color = RESET;

				if (piece != null) {
					// Piece found - use player's color
					content = piece.getOwner().getName();
					color = getPlayerColor(piece.getOwner(), state.players);
				} else if (cell instanceof SpecialCell) {
					// Special cell - use purple/blue color
					color = PURPLE;
					switch (index) {
						case 14:
							content = "(Rebirth)";
							break;
						case 25:
							content = "(Happiness)";
							break;
						case 26:
							content = "(water)";
							break;
						case 27:
							content = "(3Truthes)";
							break;
						case 28:
							content = "(re-Atoume)";
							break;
						case 29:
							content = "(Horus)";
							break;
						default:
							content = "(Special)";
							break;
					}
				} else {
					// Empty cell - use yellow/cyan color
					color = YELLOW;
					content = "(Empty)";
				}

				// Print with color
				System.out.printf(color + "[%02d:%-" + maxLen + "s]" + RESET + " ", index, content);
			}
			System.out.println();
		}
		System.out.println(
				"================================================================================================");
		System.out.println(
				"================================================================================================");
	}

	public static boolean checkGameOver(GameState state) {
		for (Player player : state.players) {
			if (state.getPiecesFor(player).isEmpty()) {
				System.out.println("Game over! " + player.getName() + " has no pieces left.");
				return true;
			}
		}
		return false;
	}

	/**
	 * Prints a compact, clean board view
	 */
	public static void printBoardCompact(GameState state) {
		System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
		System.out.println("║                              SENET BOARD                                      ║");
		System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");

		// Helper to get piece symbol at cell
		java.util.function.Function<Integer, String> getCellContent = (cellNumber) -> {
			int arrayIndex = cellNumber - 1;
			Piece piece = state.pieces.stream()
					.filter(p -> p.getPosition() == cellNumber)
					.findFirst()
					.orElse(null);

			if (piece != null) {
				String name = piece.getOwner().getName();
				return name.length() > 0 ? name.substring(0, 1).toUpperCase() : "?";
			}

			// Check for special cell
			if (arrayIndex >= 0 && arrayIndex < state.board.length) {
				Cell cell = state.board[arrayIndex];
				if (cell instanceof SpecialCell) {
					// System.out.print("special cell" + cell.index());
					switch (cell.index()) {
						case 14:
							return "R"; // Rebirth
						case 25:
							return "H"; // Happiness
						case 26:
							return "W"; // Water
						case 27:
							return "3"; // Three Truths
						case 28:
							return "A"; // Re-Atoum
						case 29:
							return "0"; // Horus
						default:
							return "D";
					}
				}
			}
			return "·";
		};

		// Row 1: 1-10
		System.out.print("║ Row 1: ");
		for (int cell = 1; cell <= 10; cell++) {
			System.out.printf("%2d[%s] ", cell, getCellContent.apply(cell));
		}
		System.out.println("║");

		// Row 2: 20-11 (reversed)
		System.out.print("║ Row 2: ");
		for (int cell = 20; cell >= 11; cell--) {
			System.out.printf("%2d[%s] ", cell, getCellContent.apply(cell));
		}
		System.out.println("║");

		// Row 3: 21-30
		System.out.print("║ Row 3: ");
		for (int cell = 21; cell <= 30; cell++) {
			System.out.printf("%2d[%s] ", cell, getCellContent.apply(cell));
		}
		System.out.println("║");

		System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");

		// Current player info
		Player current = state.getCurrentPlayer();
		System.out.printf("║ Current Player: %-65s ║\n", current != null ? current.getName() : "None");

		// Piece counts
		for (Player player : state.players) {
			int count = state.getPiecesFor(player).size();
			System.out.printf("║ %s: %d pieces%-60s ║\n",
					player.getName(), count, "");
		}

		System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝\n");
	}

	/**
	 * Prints the Senet board in a visually appealing format
	 * Shows cell numbers, pieces, and special cell markers
	 */
	public static void printBoardPretty(GameState state) {
		System.out.println("\n" + "=".repeat(80));
		System.out.println("                          SENET GAME BOARD");
		System.out.println("=".repeat(80) + "\n");

		// Helper method to get piece at a cell number (1-30)
		// Note: cellNumber 1-30 maps to array index 0-29
		java.util.function.Function<Integer, Piece> getPieceAtCell = (cellNumber) -> {
			int arrayIndex = cellNumber - 1;
			return state.pieces.stream()
					.filter(p -> p.getPosition() == cellNumber)
					.findFirst()
					.orElse(null);
		};

		// Helper to get special cell marker
		java.util.function.Function<Integer, String> getSpecialMarker = (cellNumber) -> {
			int arrayIndex = cellNumber - 1;
			if (arrayIndex < 0 || arrayIndex >= state.board.length)
				return "";

			Cell cell = state.board[arrayIndex];
			if (cell instanceof SpecialCell) {
				switch (cellNumber) {
					case 15:
						return " [REBIRTH]";
					case 26:
						return " [HAPPINESS]";
					case 27:
						return " [WATER]";
					case 28:
						return " [3TRUTHS]";
					case 29:
						return " [RE-ATOUM]";
					case 30:
						return " [HORUS]";
					default:
						return " [SPECIAL]";
				}
			}
			return "";
		};

		// Row 1: Cells 1-10 (left to right)
		System.out.println("  Row 1 (→):");
		System.out.print("  ");
		for (int cellNumber = 1; cellNumber <= 10; cellNumber++) {
			Piece piece = getPieceAtCell.apply(cellNumber);
			String marker = getSpecialMarker.apply(cellNumber);

			if (piece != null) {
				String ownerName = piece.getOwner().getName();
				String initial = ownerName.length() > 0 ? ownerName.substring(0, 1).toUpperCase() : "?";
				System.out.printf("│%2d[%s]│ ", cellNumber, initial);
			} else {
				System.out.printf("│%2d[ ]│ ", cellNumber);
			}
		}
		System.out.println();

		// Row 2: Cells 20,19,18,17,16,15,14,13,12,11 (right to left)
		System.out.println("\n  Row 2 (←):");
		System.out.print("  ");
		for (int cellNumber = 20; cellNumber >= 11; cellNumber--) {
			Piece piece = getPieceAtCell.apply(cellNumber);
			String marker = getSpecialMarker.apply(cellNumber);

			if (piece != null) {
				String ownerName = piece.getOwner().getName();
				String initial = ownerName.length() > 0 ? ownerName.substring(0, 1).toUpperCase() : "?";
				System.out.printf("│%2d[%s]│ ", cellNumber, initial);
			} else {
				System.out.printf("│%2d[ ]│ ", cellNumber);
			}
		}
		System.out.println();

		// Row 3: Cells 21-30 (left to right)
		System.out.println("\n  Row 3 (→):");
		System.out.print("  ");
		for (int cellNumber = 21; cellNumber <= 30; cellNumber++) {
			Piece piece = getPieceAtCell.apply(cellNumber);
			String marker = getSpecialMarker.apply(cellNumber);

			if (piece != null) {
				String ownerName = piece.getOwner().getName();
				String initial = ownerName.length() > 0 ? ownerName.substring(0, 1).toUpperCase() : "?";
				System.out.printf("│%2d[%s]│ ", cellNumber, initial);
			} else {
				System.out.printf("│%2d[ ]│ ", cellNumber);
			}
		}
		System.out.println();

		// Legend
		System.out.println("\n" + "-".repeat(80));
		System.out.println("  Legend:");

		// Show player pieces
		for (Player player : state.players) {
			int pieceCount = state.getPiecesFor(player).size();
			String initial = player.getName().length() > 0 ? player.getName().substring(0, 1).toUpperCase() : "?";
			System.out.printf("    [%s] = %s's piece (%d pieces)\n", initial, player.getName(), pieceCount);
		}

		// Show special cells
		System.out.println("    Special Cells:");
		System.out.println("      Cell 15  = House of Rebirth");
		System.out.println("      Cell 26  = House of Happiness");
		System.out.println("      Cell 27  = House of Water");
		System.out.println("      Cell 28  = House of Three Truths");
		System.out.println("      Cell 29  = House of Re-Atoum");
		System.out.println("      Cell 30  = House of Horus");

		System.out.println("=".repeat(80) + "\n");
	}

	/**
	 * Test method to verify game initialization
	 * Call this right after creating the game state
	 */
	public static void testGameInitialization(GameState state) {
		System.out.println("=== Testing Game Initialization ===\n");

		// 1. Test Board
		System.out.println("1. Testing Board:");
		System.out.println("   Board length: " + state.board.length + " (expected: 30)");
		boolean boardOK = state.board.length == 30;
		for (int i = 0; i < state.board.length; i++) {
			if (state.board[i] == null) {
				System.out.println("   ERROR: Board cell " + i + " is null!");
				boardOK = false;
			}
		}
		System.out.println("   Board status: " + (boardOK ? "✓ OK" : "✗ FAILED"));

		// 2. Test Special Cells
		System.out.println("\n2. Testing Special Cells:");
		Cell cell15 = state.getCell(14); // Array index 14 = Cell 15
		Cell cell26 = state.getCell(25); // Array index 25 = Cell 26
		Cell cell27 = state.getCell(26); // Array index 26 = Cell 27
		Cell cell28 = state.getCell(27); // Array index 25 = Cell 27
		System.out.println("   Cell 15 (index 14): " + (cell15 != null ? cell15.getClass().getSimpleName() : "NULL"));
		System.out.println("   Cell 26 (index 25): " + (cell26 != null ? cell26.getClass().getSimpleName() : "NULL"));
		System.out.println("   Cell 27 (index 26): " + (cell27 != null ? cell27.getClass().getSimpleName() : "NULL"));

		// 3. Test Players
		System.out.println("\n3. Testing Players:");
		System.out.println("   Number of players: " + state.players.length + " (expected: 2)");
		for (int i = 0; i < state.players.length; i++) {
			if (state.players[i] != null) {
				System.out.println("   Player " + (i + 1) + ": " + state.players[i].getName() +
						" (Strategy: "
						+ (state.players[i].getStrategy() != null
								? state.players[i].getStrategy().getClass().getSimpleName()
								: "null")
						+ ")");
			} else {
				System.out.println("   ERROR: Player " + (i + 1) + " is null!");
			}
		}

		// 4. Test Pieces
		System.out.println("\n4. Testing Pieces:");
		System.out.println("   Total pieces: " + state.pieces.size() + " (expected: 14)");

		Player player1 = state.players[0];
		Player player2 = state.players[1];

		List<Piece> player1Pieces = state.getPiecesFor(player1);
		List<Piece> player2Pieces = state.getPiecesFor(player2);

		System.out.println("   Player 1 pieces: " + player1Pieces.size() + " (expected: 7)");
		System.out.println("   Player 2 pieces: " + player2Pieces.size() + " (expected: 7)");

		// 5. Test Piece Placement (cells 1-14)
		System.out.println("\n5. Testing Piece Placement:");
		System.out.println("   Pieces on cells 1-14:");

		int[] pieceCount = new int[15]; // Index 0 unused, 1-14 for cells
		for (Piece piece : state.pieces) {
			int pos = piece.getPosition();
			if (pos >= 1 && pos <= 14) {
				pieceCount[pos]++;
				System.out.println("     Cell " + pos + ": " + piece.getOwner().getName() + "'s piece");
			} else if (pos < 0) {
				System.out.println("     WARNING: Piece at position " + pos + " (not placed!)");
			}
		}

		// Check alternating pattern
		boolean patternOK = true;
		for (int cell = 1; cell <= 14; cell++) {
			if (pieceCount[cell] != 1) {
				System.out.println("     ERROR: Cell " + cell + " has " + pieceCount[cell] + " pieces (expected: 1)");
				patternOK = false;
			}
		}
		System.out.println("   Placement pattern: " + (patternOK ? "✓ OK" : "✗ FAILED"));

		// 6. Test Current Player
		System.out.println("\n6. Testing Current Player:");
		Player current = state.getCurrentPlayer();
		System.out.println("   Current player: " + (current != null ? current.getName() : "NULL") +
				" (expected: " + state.players[0].getName() + ")");

		System.out.println("\n=== Initialization Test Complete ===\n");
	}

	public static void sendToReBirth(Piece piece, GameState state) {
		// System.out.println("sent to rebirth ::supposed to::");
		Set<Integer> occupied = new HashSet<>();
		for (Piece p : state.pieces) {
			occupied.add(p.getPosition());
		}

		// 14 stands for the house of re-birth index
		for (int pos = 14; pos >= 0; pos--) {
			if (!occupied.contains(pos)) {
				piece.moveTo(pos);
				break;
			}

		}
	}

}
