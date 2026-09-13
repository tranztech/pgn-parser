# Tranz PGN behavioral specification (draft)

This pre-1.0 document defines the intended language-neutral behavior. Statements marked **legacy gap** describe behavior not yet implemented by the Java reference.

## Document and games

Input is UTF-8 text and may begin with one BOM. A document contains zero or more games. Each game contains an optional tag section followed by movetext and normally a termination marker. Blank lines may separate sections and games. **Legacy gap:** current Java removes BOM characters anywhere and uses a double-newline heuristic for multiple games.

## Tags

A tag pair has `[`, a tag name, whitespace, a quoted value, and `]`. Tags retain source order and duplicates in the canonical AST. Seven Tag Roster names have no special parsing privilege, though consumers may validate them. Unknown tags are retained. Escaped quote/backslash behavior and duplicate-tag diagnostics will be finalized with fixtures. **Legacy gap:** current Java stores selected tags in fields, arbitrary tags in a map, discards ordering/duplicates, and removes all quotes from values.

## Movetext and whitespace

Movetext consists of move-number indications, SAN tokens, NAGs, comments, RAVs, and a termination marker separated by PGN whitespace. CRLF and LF are equivalent. Unicode is preserved. **Legacy gap:** current Java tokenizes only literal spaces after joining input lines.

## Moves and SAN

The syntax layer retains the original SAN token. Strict mode will validate supported SAN grammar; chess legality is a separate optional semantic operation requiring board state. No implementation may synthesize semantic move fields merely from an unvalidated token. Current Java accepts every otherwise unclassified token as a move.

## Results

Recognized markers are `1-0`, `0-1`, `1/2-1/2`, and `*`. A strict game result must be structurally consistent with the applicable Result tag; tolerant mode may retain the game with a diagnostic. Current Java uses the tag result when present and otherwise adopts the movetext marker.

## Comments

Brace comments are retained without braces and associated before or after a move according to source position. Semicolon comments run to line end. **Legacy gap:** Java supports only balanced brace comments, stores at most one string per attachment slot, and throws incidental runtime exceptions for unbalanced delimiters.

## NAGs

Numeric NAGs use `$` followed by digits. Symbolic annotations may map to a numeric code while retaining their source spelling. Unknown numeric NAG policy will be fixed by conformance tests. Current Java recognizes any `$`-prefixed token but stores it only when present in its catalogue; known symbolic annotations are catalogue-driven.

## Recursive Annotation Variations

Balanced parentheses contain an alternate sequence and may nest. Variations preserve source order and attach at the branch point. Strict mode diagnoses malformed delimiters. Tolerant mode may recover only at a safe boundary and reports that recovery. Variation depth is resource-limited. Current Java recursively parses balanced RAVs without a depth limit.

## Malformed and incomplete input

Strict mode reports structural errors and does not silently repair them. Tolerant mode returns all safely recoverable syntax plus diagnostics. Unknown extensions are retained only when their spelling and placement can be represented without inventing semantics; otherwise they are diagnosed. Trailing content and incomplete games are not silently discarded. Exact recovery boundaries will be fixture-driven.

## Determinism and limits

Given identical Unicode input and options, implementations produce semantically equivalent canonical AST and ordered diagnostics. Implementations must terminate and enforce configurable input, game, move, variation-depth, comment, tag, and token limits. Safe numeric defaults remain to be selected and therefore are not yet normative.
