# 0008. Optimized Block Entities: signs opt out

Date: 2026-09-29
Status: Accepted

## Context

Optimized Block Entities (OBE) bakes block entity models into chunk meshes.
For signs it draws the vanilla sign model on top of our flat models; our
blank block entity renderer doesn't stop it. OBE's own toggles can't be
relied on: its "sign" group maps to the skull option and "hanging_sign" has
no option at all.

## Decision

- A client `@Pseudo` mixin on `fr.madu59.obe.client.registry.Registry`,
  targeted by string, makes `isSupported` false for the sign and hanging
  sign block entity types and `getGroup` null for sign block states.
- No dependency on OBE, not even compile-only: descriptors are intermediary
  strings with `remap = false` and `require = 0`, so a missing OBE or a
  changed OBE API just leaves the mixin inert.
- Only active with `flat_model_rendering`; with it off, OBE optimises signs
  as usual.

## Consequences

- Signs are never OBE-optimised while our models are in use, which costs
  nothing since there's no entity model left to draw.
- If OBE renames these methods, the overlay comes back silently; recheck
  on OBE updates.
