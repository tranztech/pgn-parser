package com.tranztechnologies.pgn;

import java.util.List;
import java.util.Map;

public record PgnGame(
        List<PgnTag> tags,
        List<PgnComment> comments,
        List<PgnMove> moves,
        String result,
        List<PgnDiagnostic> diagnostics,
        Map<String, Object> metadata) {
    public PgnGame {
        tags = List.copyOf(tags);
        comments = List.copyOf(comments);
        moves = List.copyOf(moves);
        diagnostics = List.copyOf(diagnostics);
        metadata = Map.copyOf(metadata);
    }
}
