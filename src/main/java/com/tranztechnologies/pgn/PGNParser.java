package com.tranztechnologies.pgn;

import java.util.regex.Pattern;
import java.util.List;

public class PGNParser {

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\[.* \".*\"]");
    private String pgnStr;

    public PGNParser(String pgnStr) {
        setPgnStr(pgnStr);
    }

    public String getPgnStr() {
        return pgnStr;
    }

    public void setPgnStr(String pgnStr) {
        this.pgnStr = PGNUtil.getUtil().encode(pgnStr);
    }

    public PGNObject parse() {
        return parse(true);
    }

    public PGNObject parse(boolean parseNotation) {
        String pgnStr = getPgnStr();
        PGNObject pgnObject = new PGNObject(pgnStr);
        StringBuilder notation = new StringBuilder();
        for (String pgnLine : pgnStr.split("\n")) {
            if (pgnLine.equals(""))
                continue;
            if (PROPERTY_PATTERN.matcher(pgnLine).matches()) {
                int spaceIndex = pgnLine.indexOf(" ");
                PGNUtil.getUtil().add(pgnObject, pgnLine.substring(1, spaceIndex),
                        pgnLine.substring(spaceIndex, pgnLine.length() - 1).replace("\"", "").trim());
            } else {
                notation.append(pgnLine).append('\n');
            }
        }
        if (parseNotation) {
            if (notation.length() > 0) {
                pgnObject.setNotation(new NotationParser(notation.toString().trim(), pgnObject).parse());
            }
            loadMap(pgnObject);
        }
        return pgnObject;
    }

    private void loadMap(PGNObject pgnObject) {
        loadMap(pgnObject.getNotation(), pgnObject);
    }

    private void loadMap(NotationObject notationObject, PGNObject pgnObject) {
        if (notationObject != null) {
            List<MoveObject> moves = notationObject.getMoves();
            if (moves != null) {
                for (int moveIter = 0; moveIter < moves.size(); moveIter++) {
                    MoveObject moveObject = (MoveObject) moves.get(moveIter);
                    pgnObject.addMoveMap(moveObject.getMoveId(), moveObject.getParentId());
                    List<NotationObject> subVariations = moveObject.getSubVariations();
                    if (subVariations != null) {
                        for (int subIter = 0; subIter < subVariations.size(); subIter++) {
                            loadMap((NotationObject) subVariations.get(subIter), pgnObject);
                        }
                    }
                }
            }
        }
    }
}
