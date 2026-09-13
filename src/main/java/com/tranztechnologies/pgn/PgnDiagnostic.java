package com.tranztechnologies.pgn;

public record PgnDiagnostic(
        String code,
        PgnSeverity severity,
        String message,
        PgnLocation location,
        String token,
        String context) {
}
