package com.tranztechnologies.pgn;

import java.util.LinkedHashMap;
import java.util.Map;

public class PGNObject {
    private String event = "", site = "", date = "", round = "", white = "", black = "", result = "";
    private String setUp = "", plyCount = "", eventDate = "", eventType = "", eventRounds = "";
    private String eventCountry = "", source = "", sourceDate = "";
    private String fen = initialFen();
    private NotationObject notation;
    private final Map<Integer, Integer> moveMap = new LinkedHashMap<>();
    private final Map<Integer, String> moveIdMap = new LinkedHashMap<>();
    private final Map<String, String> properties = new LinkedHashMap<>();
    private int moveId;
    private String pgnString;

    public PGNObject(String pgnString) {
        this.pgnString = pgnString;
    }

    private static String initialFen() {
        return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    }

    public NotationObject getNotation() {
        return notation;
    }

    public void setNotation(NotationObject notation) {
        this.notation = notation;
    }

    public String getPgnString() {
        return pgnString;
    }

    public void setPgnString(String value) {
        pgnString = value;
    }

    public int getMoveId() {
        return moveId;
    }

    public void setMoveId(int value) {
        moveId = value;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String value) {
        event = value;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String value) {
        site = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String value) {
        date = value;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String value) {
        round = value;
    }

    public String getWhite() {
        return white;
    }

    public void setWhite(String value) {
        white = value;
    }

    public String getBlack() {
        return black;
    }

    public void setBlack(String value) {
        black = value;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String value) {
        result = value;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String value) {
        if (value == null || value.isEmpty())
            fen = initialFen();
        else
            fen = value.endsWith("0 0") ? value.replace("0 0", "0 1") : value;
    }

    public String getSetUp() {
        return setUp;
    }

    public void setSetUp(String value) {
        setUp = value;
    }

    public String getPlyCount() {
        return plyCount;
    }

    public void setPlyCount(String value) {
        plyCount = value;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String value) {
        eventDate = value;
    }

    public String getProperty(String key) {
        return properties.getOrDefault(key, "");
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Map<Integer, Integer> getMoveMap() {
        return moveMap;
    }

    public void addMoveMap(int id, int parentId) {
        moveMap.put(id, parentId);
    }

    public Map<Integer, String> getMoveIdMap() {
        return moveIdMap;
    }

    public void addMoveIdMap(int id, String move) {
        moveIdMap.put(id, move);
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String value) {
        eventType = value;
    }

    public String getEventRounds() {
        return eventRounds;
    }

    public void setEventRounds(String value) {
        eventRounds = value;
    }

    public String getEventCountry() {
        return eventCountry;
    }

    public void setEventCountry(String value) {
        eventCountry = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String value) {
        source = value;
    }

    public String getSourceDate() {
        return sourceDate;
    }

    public void setSourceDate(String value) {
        sourceDate = value;
    }
}
