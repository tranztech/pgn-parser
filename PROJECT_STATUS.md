# Tranz PGN project status

## Completed

- Inspected the complete initial repository and documented the Java parser.
- Confirmed a clean Maven build on Java 21.
- Replaced JSON-tree result models with ordinary Java objects; Jackson is limited to reading the annotation catalogue.
- Established the first shared behavior fixture and Java characterization test.
- Recorded the incremental architecture decision.
- Defined draft AST 1.0, JSON Schema, diagnostics, and parser modes.
- Added the modern `Pgn.parse` facade, immutable canonical Java records, basic defensive limits, and strict delimiter diagnostics without replacing the legacy parser.
- Added shared fixtures for a basic game, comments/NAG/RAV behavior, and malformed comments.
- Replaced space-only movetext scanning with a tokenizer supporting PGN whitespace, brace comments, semicolon comments, and safe variation tokens.
- Added deterministic canonical JSON output without JSON tree models or a JSON writer dependency.
- Added Unicode/BOM, multi-game, tokenizer, canonical JSON, and resource-limit tests.
- Adopted the target Maven artifact ID `tranz-pgn` for the pre-1.0 build.

## In Progress

- Phase 1: expand canonical behavior and conformance coverage beyond the initial contract.

## Next

- Add Unicode, BOM, multi-game, tag escaping, result mismatch, and further malformed fixtures.
- Add a canonical Java AST adapter without changing legacy parsing behavior.
- Decide whether `Round` fall-through is preserved only by the legacy adapter or fixed before the modern API.
- Complete Maven Central metadata using verified repository and developer details.

## Blocked

- Public repository URL, developer identity, security contact, and signing/publishing credentials are not present. External publishing remains intentionally disabled.

## Known Compatibility Gaps

- No strict/tolerant modes or structured diagnostics.
- No SAN or chess-legality validation.
- No streaming, source locations, resource limits, formatter, or normalizer.
- TypeScript, Python, API, and CLI implementations have not started by design.

## Decisions

- Preserve the existing Java parser as the initial behavioral reference, not the formal specification.
- Keep the root Maven layout until compatibility tests protect a later move to `java/`.
- Use shared file fixtures as the cross-language contract.
- Keep runtime parser results as Java objects; Jackson is currently used only for the bundled annotation JSON file.
