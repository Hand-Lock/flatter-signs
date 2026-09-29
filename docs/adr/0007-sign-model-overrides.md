# 0007. Unconditional blockstate overrides, every sign block

Date: 2026-09-29
Status: Accepted; supersedes the load-condition bullet of 0003

## Context

ADR 0003 gated the blockstate overrides behind a Fabric load condition so
`flat_model_rendering=false` would restore vanilla. Fabric API 0.92 only
evaluates `fabric:load_conditions` in data loaders (recipes, tags, loot),
never in blockstates, so the condition never did anything. The render-layer
list also named the 44 vanilla signs one by one, which left modded signs
drawn without cutout even when a resource pack gave them flat models.

## Decision

- The generated blockstates override vanilla unconditionally; they carry no
  load condition.
- `flat_model_rendering=false` restores vanilla through the render type
  (entity-rendered, so our models are never drawn) and by not replacing the
  block entity renderers. Only break particles still use the item texture.
- Every `AbstractSignBlock` gets the MODEL render type, the cutout layer and
  the blank renderer, modded ones included. A modded sign needs a resource
  pack that ships its flat models; the mod doesn't restrict itself to
  vanilla signs.
- Features that only make sense with the flat models (`hitbox_tweaks`,
  `glow_ink_lighting`) are off whenever `flat_model_rendering` is.

## Consequences

- A new wood type needs only an entry in the generator's `WOODS`.
- Modded signs without such a pack draw whatever their own blockstate
  points at, usually a particle-only model like vanilla's, so nothing.
  Turning `flat_model_rendering` off brings them back.
