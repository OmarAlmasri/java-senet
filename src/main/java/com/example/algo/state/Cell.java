package com.example.algo.state;

public interface Cell {
	int index();
	void onLand(Piece piece , GameState state);
}
