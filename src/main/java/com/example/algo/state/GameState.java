package com.example.algo.state;

import java.util.List;
import java.util.ArrayList;

import com.example.algo.player.Player;

public class GameState {
	/*
	 * Definitions
	 */
	public final Cell[] board;
	public final List<Piece> pieces;
	public final Player[] players;
	public int currentPlayerIndex;
	private int heuristec;
	private Piece pendingThreeTruthsPiece = null;
	private int pendingThreeTruthsPlayerIndex = -1;
	private Piece pendingAtoumPiece = null;
	private int pendingAtoumPlayerIndex = -1;
	private Piece pendingHorusPiece = null;
	private int pendingHorusPlayerIndex = -1;

	public void printCells() {
		int index = 0;
		for (Cell cell : board) {
			System.out.println("index: " + index + " class type : " + cell.getClass().getSimpleName());
			index++;
		}
	}

	public GameState(Cell[] board, List<Piece> pieces, Player[] players) {
		this.board = new Cell[30];
		System.arraycopy(board, 0, this.board, 0, Math.min(board.length, 30));

		this.pieces = new ArrayList<>(pieces);

		this.players = new Player[players.length];
		System.arraycopy(players, 0, this.players, 0, players.length);

		// Initialize current player to first player
		this.currentPlayerIndex = 0;
		this.heuristec = 0;
	}

	/*
	 * public functions
	 */
	public Cell getCell(int index) {
		// Safety check: prevent array index out of bounds
		if (index < 0 || index >= board.length) {
			return null; // Return null for out-of-bounds access (e.g., when exiting board)
		}
		return board[index];
	}

	public int getHeuristec() {
		return this.heuristec;
	}

	public Piece getPieceAtIndex(int index) {
		for (Piece piece : pieces) {
			if (piece.getPosition() == index) {
				return piece;
			}
		}
		return null;
	}

	public List<Piece> getPiecesFor(Player player) {
		return pieces.stream()
				.filter(p -> player.equals(p.getOwner()))
				.toList();
	}

	public int calculateHeuristec(Player player) {
		// the return value is wrong , we need to calculate what we want .
		List<Piece> player_pieces = getPiecesFor(player);
		int count = 0;
		for (Piece current : player_pieces) {
			count += current.getPosition();
		}
		return count / player_pieces.size();
	}

	public Player getCurrentPlayer() {
		return players[currentPlayerIndex];
	}

	public void switchPlayer() {
		currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
	}

	/*
	 * Three Truths helper functions .
	 */
	public void markThreeTruthsPiece(Piece piece, int playerIndex) {
		this.pendingThreeTruthsPiece = piece;
		this.pendingThreeTruthsPlayerIndex = playerIndex;
	}

	public Piece getPendingThreeTruthsPiece() {
		return pendingThreeTruthsPiece;
	}

	public int getPendingThreeTruthsPlayerIndex() {
		return pendingThreeTruthsPlayerIndex;
	}

	public void clearPendingThreeTruthsPiece() {
		pendingThreeTruthsPiece = null;
		pendingThreeTruthsPlayerIndex = -1;
	}

	/*
	 * re-atoum helper functions .
	 */
	public void markAtoumPiece(Piece piece, int playerIndex) {
		this.pendingAtoumPiece = piece;
		this.pendingAtoumPlayerIndex = playerIndex;
	}

	public Piece getPendingAtoumPiece() {
		return pendingAtoumPiece;
	}

	public int getPendingAtoumPlayerIndex() {
		return pendingAtoumPlayerIndex;
	}

	public void clearPendingAtoumPiece() {
		pendingAtoumPiece = null;
		pendingAtoumPlayerIndex = -1;
	}

	/*
	 * Horus helper functions .
	 */
	public void markHorusPiece(Piece piece, int playerIndex) {
		this.pendingHorusPiece = piece;
		this.pendingHorusPlayerIndex = playerIndex;
	}

	public Piece getPendingHorusPiece() {
		return pendingHorusPiece;
	}

	public int getPendingHorusPlayerIndex() {
		return pendingHorusPlayerIndex;
	}

	public void clearPendingHorusPiece() {
		pendingHorusPiece = null;
		pendingHorusPlayerIndex = -1;
	}

	/*
	 * Just a helper function .
	 */
	public void removePiece(Piece piece) {
		this.pieces.remove(piece);
	}

	/**
	 * إنشاء نسخة عميقة من حالة اللعبة (للاستخدام في البحث)
	 */
	public GameState clone() {
		// نسخ اللوحة (المراجع للخلايا نفسها - هذا مقبول لأن الخلايا لا تتغير)
		Cell[] newBoard = new Cell[30];
		System.arraycopy(this.board, 0, newBoard, 0, 30);

		// نسخ القطع (إنشاء قطع جديدة)
		List<Piece> newPieces = new ArrayList<>();
		for (Piece piece : this.pieces) {
			newPieces.add(new Piece(piece.getOwner(), piece.getPosition()));
		}

		// نسخ اللاعبين (المراجع نفسها - مقبول)
		Player[] newPlayers = new Player[this.players.length];
		System.arraycopy(this.players, 0, newPlayers, 0, this.players.length);

		// إنشاء حالة جديدة
		GameState cloned = new GameState(newBoard, newPieces, newPlayers);
		cloned.currentPlayerIndex = this.currentPlayerIndex;
		cloned.heuristec = this.heuristec;

		// نسخ الحقول الإضافية إذا كانت موجودة
		cloned.pendingThreeTruthsPiece = this.pendingThreeTruthsPiece;
		cloned.pendingThreeTruthsPlayerIndex = this.pendingThreeTruthsPlayerIndex;
		cloned.pendingAtoumPiece = this.pendingAtoumPiece;
		cloned.pendingAtoumPlayerIndex = this.pendingAtoumPlayerIndex;
		cloned.pendingHorusPiece = this.pendingHorusPiece;
		cloned.pendingHorusPlayerIndex = this.pendingHorusPlayerIndex;

		return cloned;
	}
}
