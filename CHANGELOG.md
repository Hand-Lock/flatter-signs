# Changelog

Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versions: [SemVer](https://semver.org/) (see `docs/adr/0006-release-and-privacy.md`).

## [Unreleased]

### Fixed
- With Optimized Block Entities, the vanilla sign model no longer draws
  over flat signs.
- Glow ink works with Sodium.

### Changed
- The waxed-sign sound only plays on shift + right-click, not when reading.

## [1.1.0] - 2026-09-29

### Fixed
- `wall_sign_texture_crop_height` and `wall_sign_texture_crop_offset` now
  take effect; they were ignored before.
- Hanging-sign outlines are the same at every rotation, like the model.
- Glow ink updates at once when the last ink sac in the stack is used.
- Chat respects the player's text filtering.
- With `front_only_edit` off, crouch-editing opens the side you're facing.

### Changed
- `hitbox_tweaks` only applies with `flat_model_rendering` on.
- Modded signs use the cutout layer, so resource-pack flat models for them
  render correctly.

## [1.0.0] - 2026-02-24

### Added
- Standing and hanging signs become cross models that the Mode 13h
  shaderpack billboards; wall signs lie flush with the wall and wall-hanging
  signs are flat plates. All wood types, using the item textures so any
  resource pack works.
- Right-click prints a sign's text in chat as `<Sign> …`, in its dye color;
  shift + right-click edits. Waxed signs stay readable.
- Signs are one-sided, default to white text, and glowing signs render
  fullbright.
- `config/flattersigns.json` toggles each feature, plus the wall-sign
  texture crop for resource packs with other sign shapes.
- Works on Forge through Sinytra Connector.
