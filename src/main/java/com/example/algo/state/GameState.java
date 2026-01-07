package com.example.algo.state;

import java.awt.List;
import java.util.ArrayList;

import com.example.algo.player.Player;

public class GameState {
	/*
	 * Definitions 
	 */
	private final Cell[] board = new Cell[30];
	private final List<Piece> pieces = new ArrayList<>();
	private int currentPlayerIndex;
	private int heuristec;
	
	/*
	 * public functions 
	 */
	public Cell getCell(int index) {
		return board[index];
	}
	
	public List getPiecesFor(Player player){
		return pieces.stream()
				.filter(p -> p.getOwner().equals(player)).toList();
	}
	
	public int calculateHeuristec(Player player) {
		// the return value is wrong , we need to calculate what we want .
		// PS: we need to make it for each player ( idk yet we need to talk about this ).
		return 0;
	}
}
