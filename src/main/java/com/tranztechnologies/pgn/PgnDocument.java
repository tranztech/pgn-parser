package com.tranztechnologies.pgn;

import java.util.List;

public record PgnDocument(String schemaVersion, List<PgnGame> games, List<PgnDiagnostic> diagnostics) {
    public PgnDocument {
        games = List.copyOf(games);
        diagnostics = List.copyOf(diagnostics);
    }

    public boolean success() {
        return diagnostics.stream().noneMatch(d -> d.severity() == PgnSeverity.ERROR || d.severity() == PgnSeverity.FATAL);
    }
}
