package com.tranztechnologies.pgn;

import java.util.ArrayList;
import java.util.List;

public class PGNTransformer {
    public static List<String> toArray(PGNObject pgnObject) {
        List<String> moves = new ArrayList<>();
        if (pgnObject.getNotation() != null) {
            for (MoveObject move : pgnObject.getNotation().getMoves()) moves.add(move.getMove());
        }
        return moves;
    }
}
