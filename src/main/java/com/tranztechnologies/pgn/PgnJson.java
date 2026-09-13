package com.tranztechnologies.pgn;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Deterministic canonical JSON writer with no JSON runtime dependency. */
public final class PgnJson {
    private PgnJson() { }

    public static String write(PgnDocument document) {
        StringBuilder out = new StringBuilder();
        out.append("{\"schemaVersion\":"); string(out, document.schemaVersion());
        out.append(",\"games\":"); games(out, document.games());
        out.append(",\"diagnostics\":"); diagnostics(out, document.diagnostics());
        return out.append('}').toString();
    }

    private static void games(StringBuilder out, List<PgnGame> games) {
        out.append('[');
        for (int i = 0; i < games.size(); i++) { if (i > 0) out.append(','); game(out, games.get(i)); }
        out.append(']');
    }

    private static void game(StringBuilder out, PgnGame game) {
        out.append("{\"tags\":[");
        for (int i = 0; i < game.tags().size(); i++) {
            if (i > 0) out.append(','); PgnTag tag = game.tags().get(i);
            out.append("{\"name\":"); string(out, tag.name()); out.append(",\"value\":"); string(out, tag.value()); location(out, tag.location()); out.append('}');
        }
        out.append("],\"comments\":"); comments(out, game.comments());
        out.append(",\"moves\":"); moves(out, game.moves());
        if (game.result() != null) { out.append(",\"result\":"); string(out, game.result()); }
        out.append(",\"diagnostics\":"); diagnostics(out, game.diagnostics());
        if (!game.metadata().isEmpty()) { out.append(",\"metadata\":"); map(out, game.metadata()); }
        out.append('}');
    }

    private static void moves(StringBuilder out, List<PgnMove> moves) {
        out.append('[');
        for (int i = 0; i < moves.size(); i++) {
            if (i > 0) out.append(','); PgnMove move = moves.get(i); out.append('{'); boolean comma = false;
            if (move.moveNumber() != null) { out.append("\"moveNumber\":").append(move.moveNumber()); comma = true; }
            if (move.colour() != null) { if (comma) out.append(','); out.append("\"colour\":"); string(out, move.colour()); comma = true; }
            if (comma) out.append(','); out.append("\"san\":"); string(out, move.san());
            out.append(",\"nags\":[");
            for (int n = 0; n < move.nags().size(); n++) { if (n > 0) out.append(','); nag(out, move.nags().get(n)); }
            out.append("],\"commentsBefore\":"); comments(out, move.commentsBefore());
            out.append(",\"commentsAfter\":"); comments(out, move.commentsAfter());
            out.append(",\"variations\":[");
            for (int v = 0; v < move.variations().size(); v++) { if (v > 0) out.append(','); out.append("{\"moves\":"); moves(out, move.variations().get(v).moves()); out.append('}'); }
            out.append(']'); location(out, move.location()); out.append('}');
        }
        out.append(']');
    }

    private static void nag(StringBuilder out, PgnNag nag) {
        out.append('{'); boolean comma = false;
        if (nag.code() != null) { out.append("\"code\":").append(nag.code()); comma = true; }
        if (nag.symbol() != null) { if (comma) out.append(','); out.append("\"symbol\":"); string(out, nag.symbol()); }
        out.append('}');
    }

    private static void comments(StringBuilder out, List<PgnComment> comments) {
        out.append('[');
        for (int i = 0; i < comments.size(); i++) { if (i > 0) out.append(','); out.append("{\"text\":"); string(out, comments.get(i).text()); location(out, comments.get(i).location()); out.append('}'); }
        out.append(']');
    }

    private static void diagnostics(StringBuilder out, List<PgnDiagnostic> diagnostics) {
        out.append('[');
        for (int i = 0; i < diagnostics.size(); i++) {
            if (i > 0) out.append(','); PgnDiagnostic d = diagnostics.get(i);
            out.append("{\"code\":"); string(out, d.code()); out.append(",\"severity\":"); string(out, d.severity().name()); out.append(",\"message\":"); string(out, d.message());
            location(out, d.location());
            if (d.token() != null) { out.append(",\"token\":"); string(out, d.token()); }
            if (d.context() != null) { out.append(",\"context\":"); string(out, d.context()); }
            out.append('}');
        }
        out.append(']');
    }

    private static void location(StringBuilder out, PgnLocation location) {
        if (location == null) return;
        out.append(",\"location\":{\"line\":").append(location.line()).append(",\"column\":").append(location.column())
                .append(",\"offset\":").append(location.offset()).append(",\"length\":").append(location.length()).append('}');
    }

    private static void map(StringBuilder out, Map<String, Object> values) {
        out.append('{'); Iterator<? extends Map.Entry<String, ?>> iterator = values.entrySet().iterator();
        while (iterator.hasNext()) { Map.Entry<String, ?> entry = iterator.next(); string(out, entry.getKey()); out.append(':'); value(out, entry.getValue()); if (iterator.hasNext()) out.append(','); }
        out.append('}');
    }

    private static void value(StringBuilder out, Object value) {
        if (value == null) out.append("null"); else if (value instanceof Number || value instanceof Boolean) out.append(value); else string(out, value.toString());
    }

    private static void string(StringBuilder out, String value) {
        out.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\""); case '\\' -> out.append("\\\\"); case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f"); case '\n' -> out.append("\\n"); case '\r' -> out.append("\\r"); case '\t' -> out.append("\\t");
                default -> { if (c < 0x20) out.append(String.format("\\u%04x", (int) c)); else out.append(c); }
            }
        }
        out.append('"');
    }
}
