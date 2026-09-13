package com.tranztechnologies.pgn;

import java.util.List;

public record PgnVariation(List<PgnMove> moves) {
    public PgnVariation { moves = List.copyOf(moves); }
}
