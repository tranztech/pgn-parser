package com.tranztechnologies.pgn;

import java.util.List;

public record PgnMove(
        Integer moveNumber,
        String colour,
        String san,
        List<PgnNag> nags,
        List<PgnComment> commentsBefore,
        List<PgnComment> commentsAfter,
        List<PgnVariation> variations,
        PgnLocation location) {
    public PgnMove {
        nags = List.copyOf(nags);
        commentsBefore = List.copyOf(commentsBefore);
        commentsAfter = List.copyOf(commentsAfter);
        variations = List.copyOf(variations);
    }
}
