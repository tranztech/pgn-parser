package com.tranztechnologies.pgn;

import java.util.ArrayList;
import java.util.List;

public class MoveObject {
    private Integer moveNumber = 0;
    private String move = "";
    private String colour = "";
    private final List<Annotation> annotations = new ArrayList<>();
    private String preComment = "";
    private String postComment = "";
    private final List<NotationObject> subVariations = new ArrayList<>();
    private int parentId;
    private int moveId;
    private String color = "";

    public MoveObject(int moveId) { this.moveId = moveId; }
    public Integer getMoveNumber() { return moveNumber; }
    public void setMoveNumber(Integer moveNumber) { this.moveNumber = moveNumber; }
    public String getMove() { return move; }
    public void setMove(String move) { this.move = move; }
    public String getColour() { return colour; }
    public void setColour(boolean isWhite) { this.colour = isWhite ? "WHITE" : "BLACK"; }
    public List<Annotation> getAnnotations() { return annotations; }
    public void addAnnotation(String annotation) { addAnnotation(Annotation.getAnnotation(annotation)); }
    public void addAnnotation(Annotation annotation) { if (annotation != null) annotations.add(annotation); }
    public String getPreComment() { return preComment; }
    public void setPreComment(String comment) { this.preComment = comment; }
    public String getPostComment() { return postComment; }
    public void setPostComment(String comment) { this.postComment = comment; }
    public int getMoveId() { return moveId; }
    public void setMoveId(int moveId) { this.moveId = moveId; }
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    public List<NotationObject> getSubVariations() { return subVariations; }
    public void addSubVariation(NotationObject subVariation) { subVariations.add(subVariation); }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
