package com.example.algo.setup;

import com.example.algo.state.Cell;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;

import java.util.ArrayList;
import java.util.List;

import com.example.algo.player.*;

/*
 * DEV_NOTES: the returns here are wrong , just creating the functions . 
 */
public class GameInitializer {
	public GameState createNewGame(Player[] players) {
		BoardFactory factory = new BoardFactory();
		Cell[] board = factory.createBoard();

		List<Player> playersList = new ArrayList<>();
		playersList.add(players[0]);
		playersList.add(players[1]);

		List<Piece> pieces = createPieces(playersList);

		placePiecesOnBoard(pieces);

		return new GameState(board, pieces, players);
	}

	// protected List<Player> createPlayers() {
	// 	List<Player> players = new ArrayList<>();

	// 	Player player1 = new Player("Player 1", null);
	// 	players.add(player1);

	// 	Player player2 = new Player("Player 2", null);
	// 	players.add(player2);

	// 	return players;
	// }

	protected List<Piece> createPieces(List<Player> players) {
		List<Piece> pieces = new ArrayList<>();

		Player player1 = players.get(0);
		Player player2 = players.get(1);

		// Create 7 pieces for Player 1
		for (int i = 0; i < 7; i++) {
			Piece piece = new Piece(player1, -1);
			pieces.add(piece);
		}

		// Create 7 pieces for Player 2
		for (int i = 0; i < 7; i++) {
			Piece piece = new Piece(player2, -1);
			pieces.add(piece);
		}

		return pieces;
	}

	private void placePiecesOnBoard(List<Piece> pieces) {
		int player1PieceIndex = 0;
		int player2PieceIndex = 7; // Place in the List not the Board

		for (int i = 0; i < 14; i++) {
			Piece pieceToPlace;

			int cellNumber = i + 1;

			if (cellNumber % 2 == 1) {
				pieceToPlace = pieces.get(player1PieceIndex);
				player1PieceIndex++;
			} else {
				pieceToPlace = pieces.get(player2PieceIndex);
				player2PieceIndex++;
			}

			// Move the piece to the right initial position
			pieceToPlace.moveTo(i);
		}
	}
}
