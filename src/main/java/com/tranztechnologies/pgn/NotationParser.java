package com.tranztechnologies.pgn;

import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * @author Karthikeyan
 *         Masters Chess Academy
 *         Nagercoil
 *         <p>
 *         This class is used to parse the notation in the PGN and convert it
 *         into Java objects
 */
public class NotationParser {
    private static final String MOVE_NUMBER = "MOVE_NUMBER";
    private static final String ANNOTATION = "ANNOTATION";
    private static final String MOVE = "MOVE";
    private static final String DOLLAR = "$";
    private static final String RESULT = "RESULT";
    private static final String SYMBOL = "SYMBOL";
    private static final String COLOR = "COLOR";
    private static final String HASH = "#";
    private static final Pattern moveNumberPattern = Pattern.compile("([0-9]+)\\.+");
    private String notationStr;
    private final HashMap<Integer, Integer> parenthesisMap = new HashMap<>();
    private PGNObject pgnObject;

    private HashMap<Integer, Integer> getParenthesisMap() {
        return parenthesisMap;
    }

    public PGNObject getPgnObject() {
        return pgnObject;
    }

    public void setPgnObject(PGNObject pgnObject) {
        this.pgnObject = pgnObject;
    }

    public NotationParser(String notationStr, PGNObject pgnObject) {
        setNotationStr(notationStr);
        setPgnObject(pgnObject);
    }

    public String getNotationStr() {
        return notationStr;
    }

    public void setNotationStr(String notationStr) {
        this.notationStr = notationStr;
    }

    private void putParenthesisMap(int key, int value) {
        getParenthesisMap().put(key, value);
    }

    private String findToken(String token) {
        if (moveNumberPattern.matcher(token).matches()) {
            return MOVE_NUMBER;
        } else if (token.startsWith(DOLLAR)) {
            return ANNOTATION;
        } else if (PGNUtil.getUtil().getResults().contains(token)) {
            return RESULT;
        } else if (Annotation.getAnnotationMap().containsKey(token)) {
            return SYMBOL;
        } else if (token.startsWith(HASH)) {
            return COLOR;
        } else {
            return MOVE;
        }
    }

    private int getMoveId() {
        return getPgnObject().getMoveId();
    }

    private void setMoveId(int moveId) {
        getPgnObject().setMoveId(moveId);
    }

    public NotationObject parse() {
        return parse(0);
    }

    private NotationObject parse(int parentId) {
        int moveId;
        String notationStr = getNotationStr() + " ";
        initMap(notationStr);
        NotationObject notationObject = new NotationObject();
        notationObject.setNotation(notationStr);
        moveId = getMoveId();
        setMoveId(++moveId);
        MoveObject moveObject = new MoveObject(getMoveId());
        if (parentId != 0) {
            moveObject.setParentId(parentId);
        }
        StringBuilder token = new StringBuilder();
        for (int iter = 0; iter < notationStr.length(); iter++) {
            char ch = notationStr.charAt(iter);
            switch (ch) {
                case ' ':
                    String tokenStr = token.toString();
                    if ("".equals(tokenStr)) {
                        break;
                    }
                    String tokenType = findToken(tokenStr);
                    if (MOVE_NUMBER.equals(tokenType)) {
                        if (moveObject.getMove().length() > 0) {
                            parentId = moveObject.getMoveId();
                            moveId = getMoveId();
                            setMoveId(++moveId);
                            moveObject = new MoveObject(getMoveId());
                            moveObject.setParentId(parentId);
                        }
                        int moveNumber = Integer.parseInt(tokenStr.replace(".", ""));
                        moveObject.setMoveNumber(moveNumber);
                        boolean isWhite = !tokenStr.contains("...");
                        moveObject.setColour(isWhite);
                    } else if (ANNOTATION.equals(tokenType)) {
                        moveObject.addAnnotation(tokenStr);
                    } else if (MOVE.equals(tokenType)) {
                        if (moveObject.getMove().length() > 0) {
                            int moveNumber = moveObject.getMoveNumber();
                            parentId = moveObject.getMoveId();
                            moveId = getMoveId();
                            setMoveId(++moveId);
                            moveObject = new MoveObject(getMoveId());
                            moveObject.setParentId(parentId);
                            moveObject.setColour(false);
                            moveObject.setMoveNumber(moveNumber);
                        }
                        moveObject.setMove(tokenStr);
                        getPgnObject().addMoveIdMap(moveObject.getMoveId(), moveObject.getMove());
                        notationObject.addMove(moveObject);
                    } else if (RESULT.equals(tokenType)) {
                        String result = getPgnObject().getResult();
                        if (result == null || "".equals(result)) {
                            getPgnObject().setResult(tokenStr);
                        }
                    } else if (SYMBOL.equals(tokenType)) {
                        moveObject.addAnnotation(Annotation.getAnnotationMap().get(tokenStr));
                    } else if (COLOR.equals(tokenType)) {
                        moveObject.setColor(tokenStr);
                    }
                    token = new StringBuilder();
                    break;
                case '{':
                    int end = getParenthesisMap().get(iter);
                    String comments = notationStr.substring(iter + 1, end);
                    if (moveObject.getMoveNumber().equals(0)) {
                        notationObject.setPreComment(comments);
                    } else {
                        if (moveObject.getMove().length() == 0) {
                            moveObject.setPreComment(comments);
                        } else {
                            moveObject.setPostComment(comments);
                        }
                    }
                    iter = end - 1;
                    break;
                case '(':
                    end = getParenthesisMap().get(iter);
                    String subVariation = notationStr.substring(iter + 1, end);
                    NotationParser parser = new NotationParser(subVariation, getPgnObject());
                    NotationObject subVariationObject = parser.parse(moveObject.getParentId());
                    moveObject.addSubVariation(subVariationObject);
                    iter = end - 1;
                    break;
                case ')':
                case '}':
                    break;
                default:
                    token.append(ch);
            }
        }
        return notationObject;
    }

    private void initMap(String notationStr) {
        Stack<Integer> parenthesisStack = new Stack<>();
        Stack<Integer> curlyParenthesisStack = new Stack<>();
        for (int commentIndex = 0; commentIndex < notationStr.length(); commentIndex++) {
            if (notationStr.charAt(commentIndex) == '(')
                parenthesisStack.push(commentIndex);
            else if (notationStr.charAt(commentIndex) == ')') {
                putParenthesisMap(parenthesisStack.pop(), commentIndex);
            } else if (notationStr.charAt(commentIndex) == '}') {
                putParenthesisMap(curlyParenthesisStack.pop(), commentIndex);
            } else if (notationStr.charAt(commentIndex) == '{') {
                curlyParenthesisStack.push(commentIndex);
            }
        }
    }
}
