package com.tranztechnologies.pgn;

import java.util.ArrayList;
import java.util.List;

public class NotationObject {
    private String notation = "";
    private String preComment = "";
    private final List<MoveObject> moves = new ArrayList<>();

    public String getNotation() { return notation; }
    public void setNotation(String notation) { this.notation = notation.trim(); }
    public String getPreComment() { return preComment; }
    public void setPreComment(String preComment) { this.preComment = preComment; }
    public List<MoveObject> getMoves() { return moves; }
    public void addMove(MoveObject move) { moves.add(move); }
}
