package com.tranztechnologies.pgn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiplePGNParser {
    private final String fullPGN;
    public MultiplePGNParser(String fullPGN) { this.fullPGN = PGNUtil.getUtil().encode(fullPGN); }
    public String getFullPGN() { return fullPGN; }
    public List<PGNObject> parse() throws IOException { return parse(true); }
    public List<PGNObject> parse(boolean parseNotation) throws IOException {
        List<PGNObject> parsers = new ArrayList<>();
        for (String pgn : getPGNs()) parsers.add(new PGNParser(pgn).parse(parseNotation));
        return parsers;
    }
    public String[] getPGNs() { return split(); }
    private String[] split() {
        String[] blocks = getFullPGN().split("\n\n");
        boolean notation = false;
        StringBuilder pgn = new StringBuilder();
        List<String> pgns = new ArrayList<>();
        for (int i = 0; i < blocks.length; i++) {
            String block = blocks[i].trim();
            if (block.startsWith("[") && notation) { pgns.add(pgn.toString()); pgn = new StringBuilder(); notation = false; }
            if (!block.startsWith("[")) { if (!notation) pgn.append("\n\n"); notation = true; }
            pgn.append(block);
            if (i == blocks.length - 1) pgns.add(pgn.toString());
        }
        return pgns.toArray(new String[0]);
    }
}
