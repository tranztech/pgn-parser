package com.tranztechnologies.pgn;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class Pgn {
    private Pgn() { }

    public static PgnDocument parse(String source) {
        return parse(source, PgnOptions.defaults());
    }

    public static PgnDocument parse(String source, PgnOptions options) {
        Objects.requireNonNull(options, "options");
        if (source == null) return failed("PGN_UNEXPECTED_EOF", "PGN source must not be null", PgnSeverity.FATAL, null);
        int bytes = source.getBytes(StandardCharsets.UTF_8).length;
        if (bytes > options.maxInputBytes()) {
            return failed("PGN_INPUT_LIMIT_EXCEEDED", "Input exceeds maxInputBytes", PgnSeverity.FATAL, null);
        }

        Validation validation = validateDelimiters(source, options);
        if (options.mode() == PgnMode.STRICT && validation.hasErrors()) {
            return new PgnDocument("1.0", List.of(), validation.diagnostics());
        }

        List<PgnDiagnostic> diagnostics = new ArrayList<>(validation.diagnostics());
        List<PgnGame> games = new ArrayList<>();
        try {
            for (PGNObject legacy : new MultiplePGNParser(validation.source()).parse()) {
                games.add(CanonicalAstAdapter.adapt(legacy, List.of()));
            }
        } catch (RuntimeException | IOException exception) {
            diagnostics.add(new PgnDiagnostic("PGN_UNEXPECTED_TOKEN", PgnSeverity.ERROR,
                    "Parser could not safely process the input", null, null, exception.getClass().getSimpleName()));
        }
        return new PgnDocument("1.0", games, diagnostics);
    }

    public static void parse(InputStream input, PgnOptions options, Consumer<PgnGame> consumer) throws IOException {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(consumer, "consumer");
        byte[] bytes = input.readNBytes(options.maxInputBytes() + 1);
        if (bytes.length > options.maxInputBytes()) {
            throw new IOException("PGN_INPUT_LIMIT_EXCEEDED: input exceeds maxInputBytes");
        }
        PgnDocument document = parse(new String(bytes, StandardCharsets.UTF_8), options);
        document.games().forEach(consumer);
    }

    private static Validation validateDelimiters(String source, PgnOptions options) {
        List<PgnDiagnostic> diagnostics = new ArrayList<>();
        Deque<Character> stack = new ArrayDeque<>();
        Deque<Integer> offsets = new ArrayDeque<>();
        int variationDepth = 0;
        for (int i = 0; i < source.length(); i++) {
            char character = source.charAt(i);
            if (character == '{' || character == '(') {
                stack.push(character); offsets.push(i);
                if (character == '(' && ++variationDepth > options.maxVariationDepth()) {
                    diagnostics.add(diagnostic(source, i, "PGN_VARIATION_DEPTH_EXCEEDED",
                            severity(options), "Variation depth exceeds configured limit", String.valueOf(character)));
                }
            } else if (character == '}' || character == ')') {
                char expected = character == '}' ? '{' : '(';
                if (stack.isEmpty() || stack.peek() != expected) {
                    diagnostics.add(diagnostic(source, i, character == '}' ? "PGN_UNTERMINATED_COMMENT" : "PGN_UNEXPECTED_TOKEN",
                            severity(options), "Unmatched closing delimiter", String.valueOf(character)));
                } else {
                    stack.pop(); offsets.pop();
                    if (character == ')') variationDepth--;
                }
            }
        }
        while (!stack.isEmpty()) {
            char opening = stack.pop();
            int offset = offsets.pop();
            diagnostics.add(diagnostic(source, offset, opening == '{' ? "PGN_UNTERMINATED_COMMENT" : "PGN_UNEXPECTED_EOF",
                    severity(options), "Delimiter was not terminated", String.valueOf(opening)));
        }
        return new Validation(source, diagnostics);
    }

    private static PgnSeverity severity(PgnOptions options) {
        return options.mode() == PgnMode.STRICT ? PgnSeverity.ERROR : PgnSeverity.WARNING;
    }

    private static PgnDiagnostic diagnostic(String source, int offset, String code, PgnSeverity severity, String message, String token) {
        int line = 1, column = 1;
        for (int i = 0; i < offset; i++) { if (source.charAt(i) == '\n') { line++; column = 1; } else column++; }
        return new PgnDiagnostic(code, severity, message, new PgnLocation(line, column, offset, 1), token, null);
    }

    private static PgnDocument failed(String code, String message, PgnSeverity severity, PgnLocation location) {
        return new PgnDocument("1.0", List.of(), List.of(new PgnDiagnostic(code, severity, message, location, null, null)));
    }

    private record Validation(String source, List<PgnDiagnostic> diagnostics) {
        boolean hasErrors() { return diagnostics.stream().anyMatch(d -> d.severity() == PgnSeverity.ERROR || d.severity() == PgnSeverity.FATAL); }
    }
}
