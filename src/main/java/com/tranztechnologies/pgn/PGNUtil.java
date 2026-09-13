package com.tranztechnologies.pgn;

import java.util.Arrays;
import java.util.List;

public class PGNUtil {
    private final static String UTF8_BOM = "\uFEFF";
    private static List<String> resultsList = null;
    public static PGNUtil pgnUtil = null;

    private PGNUtil() {
    }

    public static PGNUtil getUtil() {
        if (pgnUtil == null) {
            pgnUtil = new PGNUtil();
        }
        return pgnUtil;
    }

    String encode(String pgnStr) {
        return pgnStr
                .replace("\r", "")
                .replaceAll(UTF8_BOM, "")
                .replace("...", "... ")
                .replace("...  ", "... ")
                .trim();
    }

    List<String> getResults() {
        if (resultsList == null) {
            resultsList = Arrays.asList("1-0", "0-1", "1/2-1/2", "*");
        }
        return resultsList;
    }

    void add(PGNObject pgnObject, String key, String value) {
        switch (key) {
            case "Event":
                pgnObject.setEvent(value);
                break;
            case "Site":
                pgnObject.setSite(value);
                break;
            case "Date":
                pgnObject.setDate(value);
                break;
            case "Round":
                pgnObject.setRound(value);
            case "White":
                pgnObject.setWhite(value);
                break;
            case "Black":
                pgnObject.setBlack(value);
                break;
            case "Result":
                pgnObject.setResult(value);
                break;
            case "SetUp":
                pgnObject.setSetUp(value);
                break;
            case "FEN":
                pgnObject.setFen(value);
                break;
            case "PlyCount":
                pgnObject.setPlyCount(value);
                break;
            case "EventDate":
                pgnObject.setEventDate(value);
                break;
            case "EventType":
                pgnObject.setEventType(value);
                break;
            case "EventRounds":
                pgnObject.setEventRounds(value);
                break;
            case "EventCountry":
                pgnObject.setEventCountry(value);
                break;
            case "Source":
                pgnObject.setSource(value);
                break;
            case "SourceDate":
                pgnObject.setSourceDate(value);
                break;
            default:
                pgnObject.setProperty(key, value);
        }
    }
}
