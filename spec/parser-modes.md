# Parser modes

## STRICT

Strict mode accepts only syntax explicitly supported by the Tranz PGN specification. Structural errors produce `ERROR` or `FATAL` diagnostics and make the affected game or document unsuccessful. Strict mode must not reinterpret an invalid token as valid SAN merely to continue.

## TOLERANT

Tolerant mode may recover at a safe boundary such as the next move number, result marker, tag section, or game. Every material repair or skipped token produces a stable diagnostic. Recovery must not assert chess semantics that were not established.

## Future modes

`SEMANTIC` may add board-aware legality checks. `LOSSLESS` may preserve trivia and exact source spelling. Neither is currently implemented or implied by the v1 AST.

The legacy Java entry points currently behave as an undocumented permissive mode and do not yet return diagnostics. They are not equivalent to either normative mode.
