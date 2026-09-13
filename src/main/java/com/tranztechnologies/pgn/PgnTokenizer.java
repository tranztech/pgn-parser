package com.tranztechnologies.pgn;

import java.util.ArrayList;
import java.util.List;

final class PgnTokenizer {
    enum Type { WORD, COMMENT, VARIATION_START, VARIATION_END }
    record Token(Type type, String text, int offset, int length) { }

    private PgnTokenizer() { }

    static List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<>();
        int index = 0;
        while (index < source.length()) {
            char current = source.charAt(index);
            if (Character.isWhitespace(current)) { index++; continue; }
            if (current == '{') {
                int start = index++;
                int contentStart = index;
                while (index < source.length() && source.charAt(index) != '}') index++;
                tokens.add(new Token(Type.COMMENT, source.substring(contentStart, index), start, index - start + (index < source.length() ? 1 : 0)));
                if (index < source.length()) index++;
                continue;
            }
            if (current == ';') {
                int start = index++;
                int contentStart = index;
                while (index < source.length() && source.charAt(index) != '\n' && source.charAt(index) != '\r') index++;
                tokens.add(new Token(Type.COMMENT, source.substring(contentStart, index), start, index - start));
                continue;
            }
            if (current == '(' || current == ')') {
                tokens.add(new Token(current == '(' ? Type.VARIATION_START : Type.VARIATION_END,
                        String.valueOf(current), index, 1));
                index++;
                continue;
            }
            int start = index;
            while (index < source.length()) {
                current = source.charAt(index);
                if (Character.isWhitespace(current) || current == '{' || current == ';' || current == '(' || current == ')') break;
                index++;
            }
            tokens.add(new Token(Type.WORD, source.substring(start, index), start, index - start));
        }
        return tokens;
    }
}
