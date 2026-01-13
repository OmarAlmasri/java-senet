package com.example.algo.state;

import com.example.algo.player.Player;

public class Piece {
	/*
	 * Definitions
	 */
	private final Player owner;
	private int position;

	/*
	 * Constructors
	 */
	public Piece(Player owner, int position) {
		this.owner = owner;
		this.position = position;
	}

	/*
	 * public functions
	 */
	public Player getOwner() {
		return owner;
	}

	public int getPosition() {
		return position;
	}

	public void moveTo(int newPosition) {
		this.position = newPosition;
	}

}
