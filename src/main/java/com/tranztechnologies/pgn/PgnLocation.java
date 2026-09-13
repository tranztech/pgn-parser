package com.tranztechnologies.pgn;

public record PgnLocation(int line, int column, int offset, int length) {
    public PgnLocation {
        if (line < 1 || column < 1 || offset < 0 || length < 0) {
            throw new IllegalArgumentException("Invalid source location");
        }
    }
}
