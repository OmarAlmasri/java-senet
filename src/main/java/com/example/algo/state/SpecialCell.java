package com.example.algo.state;

public class SpecialCell implements Cell{
	/*
	 * definations and constructor 
	 */
	private final int index; 
	private final CellEffect effect; 
	
	public SpecialCell(int index, CellEffect effect) {
		this.index = index; 
		this.effect = effect;
	}
	/*
	 * public functions 
	 */
	public int index() {
		return index; 
	}
	public void onLand(Piece piece,GameState state) {
		effect.apply(piece , state);
	}
}
