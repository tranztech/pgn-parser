package com.tranztechnologies.pgn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CanonicalAstAdapter {
    private static final Pattern TAG = Pattern.compile("(?m)^\\s*\\[([^\\s]+)\\s+\"(.*)\"\\]\\s*$");

    private CanonicalAstAdapter() { }

    static PgnGame adapt(PGNObject source, List<PgnDiagnostic> diagnostics) {
        List<PgnTag> tags = tags(source.getPgnString());
        List<PgnMove> moves = source.getNotation() == null ? List.of() : moves(source.getNotation().getMoves());
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("legacyMoveCount", source.getMoveId());
        List<PgnComment> comments = source.getNotation() == null ? List.of() : comments(source.getNotation().getPreComment());
        return new PgnGame(tags, comments, moves, emptyToNull(source.getResult()), diagnostics, metadata);
    }

    private static List<PgnTag> tags(String text) {
        List<PgnTag> result = new ArrayList<>();
        Matcher matcher = TAG.matcher(text);
        while (matcher.find()) {
            int[] position = lineColumn(text, matcher.start());
            result.add(new PgnTag(matcher.group(1), matcher.group(2),
                    new PgnLocation(position[0], position[1], matcher.start(), matcher.end() - matcher.start())));
        }
        return result;
    }

    private static List<PgnMove> moves(List<MoveObject> source) {
        List<PgnMove> result = new ArrayList<>();
        for (MoveObject move : source) {
            List<PgnNag> nags = move.getAnnotations().stream().map(CanonicalAstAdapter::nag).toList();
            List<PgnComment> before = comments(move.getPreComment());
            List<PgnComment> after = comments(move.getPostComment());
            List<PgnVariation> variations = move.getSubVariations().stream()
                    .map(v -> new PgnVariation(moves(v.getMoves()))).toList();
            result.add(new PgnMove(move.getMoveNumber() == 0 ? null : move.getMoveNumber(),
                    emptyToNull(move.getColour()), move.getMove(), nags, before, after, variations, null));
        }
        return result;
    }

    private static PgnNag nag(Annotation annotation) {
        Integer code = null;
        if (annotation.getCode() != null && annotation.getCode().matches("\\$\\d+")) {
            code = Integer.valueOf(annotation.getCode().substring(1));
        }
        return new PgnNag(code, emptyToNull(annotation.getSymbol()));
    }

    private static List<PgnComment> comments(String value) {
        return value == null || value.isEmpty() ? List.of() : List.of(new PgnComment(value, null));
    }

    private static String emptyToNull(String value) { return value == null || value.isEmpty() ? null : value; }

    private static int[] lineColumn(String text, int offset) {
        int line = 1, column = 1;
        for (int i = 0; i < offset; i++) {
            if (text.charAt(i) == '\n') { line++; column = 1; } else column++;
        }
        return new int[] {line, column};
    }
}
