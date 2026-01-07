package com.example.algo.setup;

import com.example.algo.state.Cell;

/*
 * DEV_NOTES: the returns here are wrong , the functions are only named.
 */
public class BoardFactory {
	public Cell[] createBoard() {
		return new Cell[30];
	}
	
	protected Cell createNormalCell(int index) {
		return null ;
	}
	
	protected Cell createSpecialCell(int index) {
		return null;
	}
}
