package com.example.algo.state.effect;

import com.example.algo.state.CellEffect;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;

public class RebirthEffect implements CellEffect{
	public void apply(Piece peice , GameState state) {
    // System.out.println("standing on the rebirth effect");
		// this will be a no-op , because when a player is on this peice it will have no effect . 
	}
}
