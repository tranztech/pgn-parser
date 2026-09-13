package com.tranztechnologies.pgn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class LegacyConformanceTest {
    @Test
    void parsesBasicSharedFixture() throws Exception {
        String source = Files.readString(Path.of("conformance/valid/basic-game.pgn"));
        PGNObject game = new PGNParser(source).parse();

        assertEquals("Example", game.getEvent());
        assertEquals("Internet", game.getSite());
        assertEquals("2026.09.13", game.getDate());
        assertEquals("1", game.getRound());
        assertEquals("Alice", game.getWhite());
        assertEquals("Bob", game.getBlack());
        assertEquals("1-0", game.getResult());

        List<MoveObject> moves = game.getNotation().getMoves();
        assertEquals(List.of("e4", "e5", "Nf3", "Nc6"), moves.stream().map(MoveObject::getMove).toList());
        assertEquals(List.of(1, 2, 3, 4), moves.stream().map(MoveObject::getMoveId).toList());
        assertEquals(List.of(0, 1, 2, 3), moves.stream().map(MoveObject::getParentId).toList());
        assertEquals(List.of("WHITE", "BLACK", "WHITE", "BLACK"), moves.stream().map(MoveObject::getColour).toList());
    }

    @Test
    void canonicalFacadeMapsCommentsNagsAndVariations() throws Exception {
        String source = Files.readString(Path.of("conformance/variations/comment-nag-rav.pgn"));
        PgnDocument document = Pgn.parse(source);

        assertTrue(document.success());
        PgnGame game = document.games().getFirst();
        assertEquals("Opening note", game.comments().getFirst().text());
        assertEquals(1, game.moves().getFirst().nags().getFirst().code());
        assertEquals("d4", game.moves().getFirst().variations().getFirst().moves().getFirst().san());
    }

    @Test
    void strictModeReturnsStableDiagnosticForUnterminatedComment() throws Exception {
        String source = Files.readString(Path.of("conformance/malformed/unterminated-comment.pgn"));
        PgnDocument document = Pgn.parse(source, PgnOptions.builder().mode(PgnMode.STRICT).build());

        assertFalse(document.success());
        assertTrue(document.games().isEmpty());
        assertEquals("PGN_UNTERMINATED_COMMENT", document.diagnostics().getFirst().code());
        assertEquals(PgnSeverity.ERROR, document.diagnostics().getFirst().severity());
    }

    @Test
    void tokenizerSupportsUnicodeTabsBomAndSemicolonComments() throws Exception {
        String source = Files.readString(Path.of("conformance/encoding/unicode-whitespace.pgn"));
        PgnDocument document = Pgn.parse(source);
        PgnGame game = document.games().getFirst();

        assertEquals("Café шахматы", game.tags().getFirst().value());
        assertEquals(List.of("e4", "e5", "Nf3", "Nc6"), game.moves().stream().map(PgnMove::san).toList());
        assertEquals(" first move", game.moves().getFirst().commentsAfter().getFirst().text());
    }

    @Test
    void parsesMultipleGamesAndWritesValidDeterministicJson() throws Exception {
        String source = Files.readString(Path.of("conformance/multi-game/two-games.pgn"));
        PgnDocument document = Pgn.parse(source);

        assertEquals(2, document.games().size());
        assertEquals("First", document.games().get(0).tags().getFirst().value());
        assertEquals("Second", document.games().get(1).tags().getFirst().value());
        String json = PgnJson.write(document);
        new ObjectMapper().readTree(json);
        assertEquals(json, PgnJson.write(document));
    }

    @Test
    void enforcesInputAndVariationDepthLimits() {
        PgnDocument oversized = Pgn.parse("1. e4 *", PgnOptions.builder().maxInputBytes(3).build());
        assertEquals("PGN_INPUT_LIMIT_EXCEEDED", oversized.diagnostics().getFirst().code());

        PgnDocument tooDeep = Pgn.parse("1. e4 (((1. d4))) *",
                PgnOptions.builder().maxVariationDepth(2).build());
        assertFalse(tooDeep.success());
        assertTrue(tooDeep.games().isEmpty());
        assertEquals("PGN_VARIATION_DEPTH_EXCEEDED", tooDeep.diagnostics().getFirst().code());
    }
}
