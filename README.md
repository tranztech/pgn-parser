# Tranz PGN

**One PGN. One AST. Every platform.**

Tranz PGN is a standards-focused Portable Game Notation parser for chess applications. The current Java library parses PGN into ordinary Java objects and exposes a versioned, language-neutral AST designed for future TypeScript, Python, CLI, and HTTP implementations.

> Project status: active pre-1.0 development. Java is available; TypeScript, Python, REST, and CLI packages are not released yet.

## Quick start

Build and install the current snapshot into your local Maven repository:

```bash
./mvnw clean install
```

Add it to another Maven project:

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/tranztech/pgn-parser</url>
  </repository>
</repositories>
```

GitHub Packages requires credentials in the consumer's Maven `settings.xml` under the matching `github` server ID. Then add:

```xml
<dependency>
  <groupId>com.tranztechnologies</groupId>
  <artifactId>pgn-parser</artifactId>
  <version>1.0.0</version>
</dependency>
```

Parse a game:

```java
import com.tranztechnologies.pgn.Pgn;
import com.tranztechnologies.pgn.PgnDocument;

String source = """
    [Event "Example"]
    [White "Alice"]
    [Black "Bob"]
    [Result "1-0"]

    1. e4 e5 2. Nf3 Nc6 1-0
    """;

PgnDocument document = Pgn.parse(source);
document.games().getFirst().moves()
    .forEach(move -> System.out.println(move.san()));
```

## Strict and tolerant parsing

The default is tolerant parsing. Strict mode returns structured diagnostics for malformed supported syntax:

```java
import com.tranztechnologies.pgn.PgnMode;
import com.tranztechnologies.pgn.PgnOptions;

PgnDocument document = Pgn.parse(
    source,
    PgnOptions.builder()
        .mode(PgnMode.STRICT)
        .maxInputBytes(2 * 1024 * 1024)
        .maxVariationDepth(64)
        .build()
);

if (!document.success()) {
    document.diagnostics().forEach(diagnostic ->
        System.err.println(diagnostic.code() + ": " + diagnostic.message()));
}
```

## Canonical JSON

`PgnJson` produces deterministic canonical JSON without using a JSON dependency for parser models or serialization:

```java
String json = PgnJson.write(document);
```

Jackson is currently used only to load the bundled `annotations.json` catalogue. Parsed results are Java records, POJOs, lists, and maps.

## Streaming callback

```java
try (InputStream input = Files.newInputStream(Path.of("games.pgn"))) {
    Pgn.parse(input, PgnOptions.defaults(), game -> process(game));
}
```

The current callback API enforces the input-byte limit but still buffers the bounded input internally. Incremental I/O is planned before the streaming feature is considered complete.

## Feature status

| Feature | Status |
|---|---|
| Tag pairs and unknown tags | Supported |
| Main-line movetext | Supported |
| Multiple games | Supported for documented fixture patterns |
| Brace comments | Supported |
| Semicolon comments | Supported |
| Numeric and known symbolic NAGs | Supported |
| RAV and nested variations | Supported with configured depth checks |
| UTF-8, Unicode, BOM, CRLF, tabs | Supported |
| Canonical AST and deterministic JSON | Draft 1.0 implemented in Java |
| Structured diagnostics | Initial delimiter and resource diagnostics |
| Strict/tolerant modes | Initial implementation |
| SAN syntax validation | Not yet implemented |
| Chess legality validation | Not implemented |
| Exact source locations for moves/comments | Not yet implemented |
| True incremental streaming | Not yet implemented |
| Formatter/normalizer | Not yet implemented |
| TypeScript, Python, REST API, CLI | Planned after conformance stabilization |

## Architecture

- `src/main/java`: legacy-compatible parser plus the modern Java façade.
- `spec/`: language-neutral behavior, AST schema, diagnostics, and parser modes.
- `conformance/`: shared inputs and expected semantic behavior.
- `docs/internal/`: implementation analysis and engineering notes.
- `docs/adr/`: significant architecture decisions.

The legacy `PGNParser` remains available for compatibility. New applications should use `Pgn.parse`, which returns the canonical model and diagnostics.

## Testing

```bash
./mvnw clean test
```

Every parser bug fix should add a shared conformance fixture. The same corpus will be used by Java, TypeScript, Python, and the REST API so identical input and options produce equivalent semantics.

## Specification

- [Behavioral specification](spec/specification.md)
- [Canonical AST](spec/ast.md)
- [JSON Schema](spec/ast-schema.json)
- [Diagnostics](spec/diagnostics.md)
- [Parser modes](spec/parser-modes.md)
- [Compatibility matrix](spec/compatibility.md)

## Roadmap

The immediate priorities are broader conformance coverage, SAN syntax validation, safe tolerant recovery, true streaming, and stabilization of the Java API. TypeScript and Python ports begin only after the canonical contract is sufficiently protected by shared tests. Current progress and known gaps are tracked in [PROJECT_STATUS.md](PROJECT_STATUS.md).

## Contributing

Contributions are welcome during the pre-1.0 phase. Please include tests and, for parsing behavior changes, a conformance fixture. Public contribution and security policies will be added during the open-source project-polish phase.

## License

Apache License 2.0 is the intended project license. The legal files have not yet been added and the repository must not be treated as formally licensed until they are reviewed and committed.
