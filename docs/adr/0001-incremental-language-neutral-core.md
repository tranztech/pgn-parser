# ADR 0001: Incremental language-neutral core

## Status

Accepted

## Context

The repository contains a small working Java parser with undocumented behavior and no tests. Tranz PGN requires equivalent Java, TypeScript, Python, and HTTP results, but making the Java object model the specification would couple every implementation to legacy details and bugs.

## Decision

Treat the current Java implementation as the initial behavioral reference. First capture behavior in shared fixtures. Define a versioned language-neutral AST, diagnostics model, parser modes, and canonical JSON independently. Add adapters and modern APIs alongside legacy Java entry points. Delay moving the Maven module until root build compatibility is protected.

## Alternatives

- Rewrite the Java parser immediately: rejected because behavior is unprotected and users may depend on it.
- Make `PGNObject` the canonical schema: rejected because it exposes Java-specific mutability and lacks diagnostics and locations.
- Build language ports first: rejected because no stable cross-language contract exists yet.

## Consequences

Initial progress includes documentation and characterization work before new language ports. Some legacy bugs may be recorded temporarily, but the canonical specification can explicitly reject or isolate them. Adapters add a small amount of code while allowing independent implementation evolution.
