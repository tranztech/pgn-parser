# Tranz PGN conformance corpus

Each manifest entry points to a UTF-8 PGN input and a deterministic expected semantic result. During Phase 0, `referenceModel: legacy-java-v0` records observed Java behavior and must not be mistaken for canonical AST v1. Phase 1 will add canonical AST fixtures and explicit normalization rules.

Every parsing bug fix should add or update a fixture. Implementations must consume the same inputs; language-specific tests must not silently fork expected behavior.
