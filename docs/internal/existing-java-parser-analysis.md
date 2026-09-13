# Existing Java parser analysis

Status: Phase 0 baseline, 2026-09-13. This document describes the code as observed; it is not yet the Tranz PGN specification.

## 1. Current architecture

The repository is a single Maven JAR using Java 21. All production code is in `com.tranztechnologies.pgn`. There is no lexer, parser generator, chess engine, web framework, CLI framework, or module boundary. `PGNParser` separates headers from movetext line-by-line, then delegates movetext to `NotationParser`. `MultiplePGNParser` heuristically splits a string into games. Parsed output is a mutable graph of ordinary Java objects.

Jackson Databind is the only runtime dependency and is used only by `Annotation` to load the bundled `annotations.json` NAG catalogue. The parser result itself does not depend on JSON tree types.

## 2. Current public APIs

- `new PGNParser(String).parse()` parses headers and movetext into `PGNObject`.
- `PGNParser.parse(false)` parses headers only.
- `new MultiplePGNParser(String).parse()` returns `List<PGNObject>`.
- `NotationParser.parse()` is public and can parse movetext against a supplied `PGNObject`.
- `PGNTransformer.toArray(PGNObject)` returns the main-line SAN/token strings.
- `Annotation.getAnnotation(String)` and `getAnnotationMap()` expose the NAG catalogue.
- Model classes expose mutable getters/setters and mutable `List`/`Map` instances.
- `Worker.main` is a diagnostic stub, not a usable CLI.

There is no façade such as `Pgn.parse`, options object, streaming API, validation API, diagnostics API, formatter, or stable canonical serializer.

## 3. Internal parsing pipeline

1. `PGNUtil.encode` removes carriage returns and every UTF-8 BOM character, then inserts a space after `...` using literal replacements.
2. `PGNParser` splits on LF. Empty lines are discarded.
3. Lines matching `\[.* \".*\"\]` are treated as tags. The first space divides name from value; every double quote in the value portion is removed.
4. All other lines are concatenated with spaces as movetext.
5. `NotationParser.initMap` scans characters and pairs braces and parentheses using stacks.
6. The parser scans characters again. Only the literal space ends a token. It classifies tokens as move number, numeric NAG, result, known symbolic NAG, `#` colour extension, or move.
7. Move tokens are stored verbatim; no SAN grammar or board-state validation occurs.
8. Brace comments are attached before the game, before a move, or after a move according to the current mutable move state.
9. Parenthesized RAV text is recursively parsed and attached to the current move object.
10. A final traversal builds move-id-to-parent and move-id-to-token maps.

## 4. Supported PGN features

- Common tag pairs plus arbitrary unknown tags.
- LF and CRLF input; BOM removal.
- Main-line move numbers in forms matched by `[0-9]+` followed by one or more periods.
- White/black colour inferred from one period versus a token containing `...`.
- Result markers `1-0`, `0-1`, `1/2-1/2`, and `*`.
- Brace comments.
- Numeric NAG tokens beginning with `$`; known codes resolve through the bundled catalogue.
- Known symbolic NAGs from that catalogue.
- Recursive parenthesized variations, including nesting when delimiters are balanced.
- Multiple games when separated in the particular blank-line/tag pattern expected by `MultiplePGNParser`.
- Unicode is retained in Java strings.

## 5. Unsupported or unclear features

- SAN is not parsed or validated; any otherwise unclassified token becomes a move.
- No chess legality, board state, piece, square, capture, promotion, check, checkmate, castling, or en-passant model.
- Semicolon comments and `%` escape lines are not recognized.
- Escaped quotes/backslashes in tag values are not parsed according to a formal grammar.
- Tabs and other whitespace do not delimit movetext tokens.
- No source locations, diagnostics, recovery policy, limits, streaming, lossless syntax, formatting, or normalization.
- Clock/evaluation directives inside comments have no structured representation.
- Duplicate tag policy and tag ordering are not preserved: known tags overwrite fields and unknown tags overwrite map entries.
- Trailing non-PGN content becomes move tokens.

## 6. Error-handling behaviour

There is no explicit validation result. Most malformed input is accepted as move text. Unbalanced closing braces/parentheses can throw `EmptyStackException`; unbalanced opening delimiters can later cause null unboxing or substring failures. Annotation resource failures become `UncheckedIOException`. Null input throws `NullPointerException`. Numeric move overflow throws `NumberFormatException`. No error includes a stable code or source location.

## 7. Data model

`PGNObject` contains selected header strings, arbitrary properties, raw normalized source, one `NotationObject`, global move counter, and two derived move maps. `NotationObject` contains normalized notation, one pre-comment, and ordered moves. `MoveObject` contains move number, colour, verbatim move token, annotations, before/after comments, variations, parent/move IDs, and a `#` extension value. `Annotation` contains catalogue name, symbol, and code.

The model is mutable. Empty strings and empty collections are defaults; `notation` is null when absent or skipped. Source locations and diagnostics are absent.

## 8. Inferred performance characteristics

Single-game parsing is approximately linear in input length plus a final traversal. Delimiter pairing is linear. Token accumulation and substring extraction allocate strings. Variations recurse and can exhaust the stack. The entire input and AST remain in memory. Multiple-game splitting copies blocks and is not streaming. The annotation catalogue is lazily cached, but its unsynchronized static initialization is not robust under concurrent first use.

## 9. Technical debt discovered

- No tests existed before the Phase 0 characterization fixture.
- `Round` handling falls through and also sets `White` to the round value.
- `MultiplePGNParser` depends on exact double-newline grouping and removes separators while concatenating blocks.
- Delimiter scanning assumes balanced and properly nested delimiters.
- Move token classification is permissive and space-specific.
- Public implementation classes and mutable internals are exposed.
- Naming is inconsistent (`colour` and `color`, `PGN*` acronym style).
- `Worker` and Spring-named `application.properties` are unused remnants.
- Maven Central metadata is incomplete and coordinates are still `com.tranztechnologies:pgn:0.0.1-SNAPSHOT`.

## 10. Behaviour to preserve initially

- Existing `PGNParser`, `MultiplePGNParser`, model accessors, and `PGNTransformer` entry points.
- Verbatim move token storage and stable move order.
- Existing move/parent ID construction.
- Current brace-comment and RAV attachment for well-formed inputs.
- Known numeric and symbolic annotation lookup.
- Default initial FEN and correction of a FEN ending in `0 0`.
- BOM/CR normalization and arbitrary unknown-tag access.

## 11. Behaviour that should probably improve

Fix the `Round` fall-through under a compatibility decision; introduce deterministic diagnostics instead of runtime exceptions; tokenize all specified whitespace; define escaping and duplicate tags; add strict/tolerant modes and limits; distinguish syntax validation from chess semantics; make caches thread-safe; and provide a canonical, language-neutral AST adapter without forcing the legacy model to become the specification.

## 12. Potential breaking changes

Changing malformed-input acceptance, whitespace tokenization, comment attachment, variation parent IDs, duplicate tags, mutable collection exposure, empty-string/null defaults, checked exceptions, coordinates, Java baseline, or output field naming can break callers. Moving sources under `java/` can also break repository-level build commands. These changes require characterization fixtures, compatibility adapters, and release notes before implementation.

## Proposed monorepo migration strategy

1. Keep the current Maven layout at the repository root while behavior is characterized; add specifications and shared fixtures beside it.
2. Define canonical AST v1, omission rules, diagnostics, and normalization independently of `PGNObject`.
3. Add a Java adapter from the legacy object graph to canonical AST and run it against shared fixtures.
4. Introduce the modern Java façade alongside, not in place of, legacy entry points.
5. Move the Maven module to `java/` only after root-level wrapper commands delegate to it and compatibility CI passes.
6. Add TypeScript and Python implementations only after the initial schema and fixture results are accepted.
7. Add cross-language comparison before API/CLI layers; those layers consume an implementation and never duplicate parsing.

## Primary risks

The largest risks are accidentally specifying current bugs, changing undocumented attachment/ID behavior, presenting token acceptance as SAN validation, stack/memory exhaustion on hostile input, and allowing language ports to drift before canonical omission and normalization rules exist.
