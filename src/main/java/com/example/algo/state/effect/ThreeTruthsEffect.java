package com.example.algo.state.effect;

import com.example.algo.state.CellEffect;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;

public class ThreeTruthsEffect implements CellEffect {
    public void apply(Piece piece, GameState state) {
        int playerIndex = -1;
        for (int i = 0; i < state.players.length; i++) {
            if (state.players[i].equals(piece.getOwner())) {
                playerIndex = i;
                break;
            }
        }
        state.markThreeTruthsPiece(piece, playerIndex);
    }
}
