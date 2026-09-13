package com.tranztechnologies.pgn;

import java.util.List;
import java.util.regex.Pattern;

/** Parses PGN movetext into the legacy Java object model. */
public class NotationParser {
    private static final Pattern MOVE_NUMBER = Pattern.compile("([0-9]+)\\.+");
    private String notationStr;
    private PGNObject pgnObject;

    public NotationParser(String notationStr, PGNObject pgnObject) {
        setNotationStr(notationStr);
        setPgnObject(pgnObject);
    }

    public String getNotationStr() { return notationStr; }
    public void setNotationStr(String notationStr) { this.notationStr = notationStr; }
    public PGNObject getPgnObject() { return pgnObject; }
    public void setPgnObject(PGNObject pgnObject) { this.pgnObject = pgnObject; }

    public NotationObject parse() {
        List<PgnTokenizer.Token> tokens = PgnTokenizer.tokenize(notationStr);
        Cursor cursor = new Cursor();
        return parse(tokens, cursor, 0, notationStr.trim());
    }

    private NotationObject parse(List<PgnTokenizer.Token> tokens, Cursor cursor, int parentId, String notation) {
        NotationObject result = new NotationObject();
        result.setNotation(notation);
        MoveObject current = newMove(parentId);

        while (cursor.index < tokens.size()) {
            PgnTokenizer.Token token = tokens.get(cursor.index++);
            if (token.type() == PgnTokenizer.Type.VARIATION_END) break;
            if (token.type() == PgnTokenizer.Type.COMMENT) {
                attachComment(result, current, token.text());
                continue;
            }
            if (token.type() == PgnTokenizer.Type.VARIATION_START) {
                int start = cursor.index;
                NotationObject variation = parse(tokens, cursor, current.getParentId(), variationText(tokens, start, cursor.index));
                current.addSubVariation(variation);
                continue;
            }

            String value = token.text();
            if (MOVE_NUMBER.matcher(value).matches()) {
                if (!current.getMove().isEmpty()) current = newMove(current.getMoveId());
                current.setMoveNumber(Integer.parseInt(value.replace(".", "")));
                current.setColour(!value.contains("..."));
            } else if (isResult(value)) {
                if (pgnObject.getResult().isEmpty()) pgnObject.setResult(value);
            } else if (value.startsWith("$")) {
                current.addAnnotation(value);
            } else if (Annotation.getAnnotationMap().containsKey(value)) {
                current.addAnnotation(Annotation.getAnnotationMap().get(value));
            } else if (value.startsWith("#")) {
                current.setColor(value);
            } else {
                if (!current.getMove().isEmpty()) {
                    int previousId = current.getMoveId();
                    int number = current.getMoveNumber();
                    current = newMove(previousId);
                    current.setMoveNumber(number);
                    current.setColour(false);
                }
                current.setMove(value);
                pgnObject.addMoveIdMap(current.getMoveId(), value);
                result.addMove(current);
            }
        }
        return result;
    }

    private MoveObject newMove(int parentId) {
        pgnObject.setMoveId(pgnObject.getMoveId() + 1);
        MoveObject move = new MoveObject(pgnObject.getMoveId());
        move.setParentId(parentId);
        return move;
    }

    private static void attachComment(NotationObject notation, MoveObject move, String comment) {
        if (move.getMoveNumber() == 0) notation.setPreComment(comment);
        else if (move.getMove().isEmpty()) move.setPreComment(comment);
        else move.setPostComment(comment);
    }

    private static boolean isResult(String token) {
        return token.equals("1-0") || token.equals("0-1") || token.equals("1/2-1/2") || token.equals("*");
    }

    private static String variationText(List<PgnTokenizer.Token> tokens, int start, int ignoredEnd) {
        StringBuilder text = new StringBuilder();
        for (int i = start; i < tokens.size() && tokens.get(i).type() != PgnTokenizer.Type.VARIATION_END; i++) {
            if (!text.isEmpty()) text.append(' ');
            text.append(tokens.get(i).text());
        }
        return text.toString();
    }

    private static final class Cursor { private int index; }
}
