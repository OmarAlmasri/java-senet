package com.example.algo.state.effect;

import com.example.algo.state.CellEffect;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;

public class HappinessEffect implements CellEffect {
    public void apply(Piece peice, GameState state) {
       System.out.println("inside the HappinessEffect apply function"); 
    }
}
