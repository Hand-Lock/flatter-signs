# Flatter Signs — spec

## Vision

Make signs billboard-ready for the Mode 13h shaderpack, and keep them
readable once their text can no longer be drawn: a billboarded sign has no
face to write on, so the text goes to chat.

## Principles

- Opinionated defaults: everything on, tuned for Mode 13h.
- Every feature can be toggled on its own, so the mod stays useful without
  the shaderpack.
- Resource-pack friendly: models use the sign *item* textures; the wall-sign
  crop is configurable for packs with other silhouettes.
- Vanilla mechanics are preserved and made visible: dye colors the chat
  text, glow ink makes the sign glow, wax locks editing but not reading.

## Compatibility

| Platform          | Minecraft | Status                                          |
| ----------------- | --------: | ----------------------------------------------- |
| Fabric            |    1.20.1 | Native target                                   |
| Forge             |    1.20.1 | Via Sinytra Connector + Forgified Fabric API    |
| Without Mode 13h  |    1.20.1 | Works; signs show as cross models, no billboard |

## Current features

Config keys in `config/flattersigns.json`, defaults in parentheses:

- `front_only_edit` (true): signs have one side; back reads and writes go
  to the front.
- `flat_model_rendering` (true): cross/flat block models replace the vanilla
  sign rendering; text is not drawn in the world.
- `hitbox_tweaks` (true): outlines fit the flat models.
- `crouch_edit_and_chat` (true): right-click prints the text in chat as
  `<Sign> …` in the dye color; shift + right-click (or an empty sign) opens
  the editor; waxed signs can still be read.
- `default_white_text` (true): new signs have white text instead of black.
- `glow_ink_lighting` (true): glowing signs render fullbright and update at
  once.
- `wall_sign_texture_crop_height` (11, 1..16): pixel rows of the item
  texture shown on wall signs.
- `wall_sign_texture_crop_offset` (0, 0..16): rows skipped before the crop.

## Mode 13h contract

Mode 13h billboards block ID 10956, which its `block.properties` maps to the
standing and ceiling-hanging sign blocks, gated by its `FLATTER_SIGNS`
option. The shaderpack
expects our standing and ceiling-hanging sign models to be crosses. Don't
change that shape or ask for a different ID without a matching Mode 13h
change and a major version.

## Roadmap

- **R1. Port to 1.21.1+** (Mode 13h roadmap R6). Needs an ADR first:
  one codebase or branches, and what happens to the Forge path.

## Non-goals

- A config GUI.
- Back-side text.
- Billboarding wall-hanging signs (their shape doesn't allow it; they stay
  flat plates).
- Any dependency beyond Fabric API.

## Distribution

- Modrinth: `flatter-signs` (project `lcVZmjEg`).
- Source: https://github.com/Hand-Lock/flatter-signs
- License: LGPL-3.0-or-later.
