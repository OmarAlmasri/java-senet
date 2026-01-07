package com.example.algo.state;

public class NormalCell implements Cell{
	/*
	 * defenitions and constructor 
	 */
	private final int index; 
	
	public NormalCell(int index) {
		this.index = index; 
	}
	/*
	 * public functions 
	 */
	public int index() {
		return index; 
	}
	
	public void onLand(Piece piece , GameState state) {
		// no operations 
	}
}
