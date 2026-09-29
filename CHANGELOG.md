# Changelog

Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versions: [SemVer](https://semver.org/) (see `docs/adr/0006-release-and-privacy.md`).

## [Unreleased]

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
