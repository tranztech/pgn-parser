# Diagnostics

A diagnostic has `code`, `severity`, and `message`, with optional `location`, `token`, and `context`. Severity is one of `INFO`, `WARNING`, `ERROR`, or `FATAL`. Codes are stable machine-readable identifiers; messages may improve without a schema-version change.

Initial reserved codes are `PGN_INVALID_TAG`, `PGN_UNTERMINATED_TAG`, `PGN_UNTERMINATED_COMMENT`, `PGN_INVALID_MOVE_NUMBER`, `PGN_INVALID_SAN`, `PGN_UNEXPECTED_TOKEN`, `PGN_UNEXPECTED_EOF`, `PGN_INVALID_RESULT`, `PGN_VARIATION_DEPTH_EXCEEDED`, and `PGN_INPUT_LIMIT_EXCEEDED`.

A location may contain one-based `line` and `column`, zero-based UTF-16-independent byte or code-point `offset` as defined by the accepting API, and non-negative `length`. Until the offset unit is finalized, implementations must omit `offset` rather than emit incompatible values. Missing location data is permitted during the compatibility phase.

Invalid PGN is a normal parse outcome and must not be represented as an unexpected server failure. Tolerant recovery must always be visible through diagnostics.
