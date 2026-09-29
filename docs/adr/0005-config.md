# 0005. Config: one flat JSON file

Date: 2026-09-29
Status: Accepted

## Context

Players without Mode 13h want to keep only parts of the mod. A config
library or GUI would be a dependency for eight values.

## Decision

- One file, `config/flattersigns.json`, with snake_case keys and primitive
  values only. No nesting, no config library, no GUI.
- Numbers are clamped on load. Missing or malformed values fall back to
  defaults.
- The file is rewritten on every load, so new keys appear after updates.
- Every behavior has its own toggle; mixins check it first and return
  early.

## Consequences

- Changes need a game restart.
- Renaming or removing a key silently resets it for players, so it is a
  major version (ADR 0006).
