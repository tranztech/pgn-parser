# Canonical AST 1.0 draft

The canonical interchange root is a `PgnDocument` with `schemaVersion`, ordered `games`, and ordered `diagnostics`. A game contains ordered `tags`, game-level comments, ordered main-line `moves`, an optional result, game diagnostics, and optional metadata. Unknown tags remain ordered tag entries rather than being folded into implementation-specific fields.

A tag contains `name` and `value`, with optional source location. A move contains syntax known from the source: optional move number and colour, required verbatim `san`, ordered NAGs, before/after comments, and ordered variations. A variation contains ordered moves. A comment contains text and optional location. A NAG contains its numeric code when known and may retain its source symbol. A diagnostic follows `diagnostics.md`.

Chess-semantic fields such as piece, origin, destination, capture, promotion, check, checkmate, castling, and en passant are omitted unless a board-aware implementation has actually established them. Their absence never means false. Source locations are optional during initial adoption.

Canonical JSON uses lower camel case, arrays preserve source order, absent optional values are omitted, and no timestamps or implementation class metadata are emitted. Object key order is not semantically significant. Canonical comparison recursively compares objects by key and arrays by order.

Legacy `PGNObject` is mapped through an adapter; its move IDs and derived maps are compatibility metadata and are not canonical syntax semantics.
