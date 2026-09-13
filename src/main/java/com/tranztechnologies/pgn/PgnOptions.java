package com.tranztechnologies.pgn;

public final class PgnOptions {
    private final PgnMode mode;
    private final int maxInputBytes;
    private final int maxVariationDepth;

    private PgnOptions(Builder builder) {
        mode = builder.mode;
        maxInputBytes = builder.maxInputBytes;
        maxVariationDepth = builder.maxVariationDepth;
    }

    public static Builder builder() { return new Builder(); }
    public static PgnOptions defaults() { return builder().build(); }
    public PgnMode mode() { return mode; }
    public int maxInputBytes() { return maxInputBytes; }
    public int maxVariationDepth() { return maxVariationDepth; }

    public static final class Builder {
        private PgnMode mode = PgnMode.TOLERANT;
        private int maxInputBytes = 10 * 1024 * 1024;
        private int maxVariationDepth = 256;

        public Builder mode(PgnMode mode) { this.mode = java.util.Objects.requireNonNull(mode); return this; }
        public Builder maxInputBytes(int value) { if (value < 1) throw new IllegalArgumentException("maxInputBytes must be positive"); maxInputBytes = value; return this; }
        public Builder maxVariationDepth(int value) { if (value < 1) throw new IllegalArgumentException("maxVariationDepth must be positive"); maxVariationDepth = value; return this; }
        public PgnOptions build() { return new PgnOptions(this); }
    }
}
