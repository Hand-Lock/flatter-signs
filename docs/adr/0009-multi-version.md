# 0009. Multi-version: one codebase with Stonecutter

Date: 2026-09-29
Status: Accepted

Supersedes 0002, whose Connector decisions it carries forward.

## Context

Mode 13h's roadmap (R6) moves to 1.21.1 and later, while its 1.20.1
players stay. Between 1.20.1 and 1.21.1 only a handful of APIs our code
touches changed: `Identifier` construction, custom payload networking,
`AbstractSignBlock.onUse` losing its `Hand`, `readNbt` gaining a registry
lookup, the model-loading context id, `ModelElementFace` becoming a record.
The rest of the mod, assets included, is identical.

Forge proper has no Sinytra Connector past 1.20.1; on 1.21.1 Connector
and Forgified Fabric API exist for NeoForge only.

## Decision

- One codebase, built with Stonecutter (Gradle plugin, Kotlin DSL). Shared
  `src/`; one Gradle subproject per Minecraft version under `versions/<mc>/`
  with its Yarn and Fabric API versions; `./gradlew build` builds them all.
- Where an API differs, the code carries a Stonecutter comment condition
  (`//? if >=1.20.5 {`) on the Minecraft version that introduced the
  change. No wrapper layer: conditions stay next to the call they affect.
- The committed sources are those of `vcsVersion`, the newest version
  (1.21.1). Switching to another version rewrites comments in `src/`, so
  the active version must be reset before committing; `tools/check.sh`
  enforces it.
- Java bytecode follows Minecraft: 17 up to 1.20.4, 21 from 1.20.5.
- Yarn stays. It ends at 1.21.11; supporting 26.x needs Mojmap and a new
  ADR.
- Stonecutter is a build plugin, not a mod dependency: every jar still
  depends on Fabric API alone.
- The Forge path is Connector wherever Connector exists: Forge and NeoForge
  on 1.20.1, NeoForge only on 1.21.1. Where Connector remaps or skips a code
  path, mixins add extra injects with `require = 0` against the Mojmap and
  intermediary names of each version (`load`/`loadAdditional`,
  `handleUpdateTag`, `method_26208`…), so a missing target never crashes.
- Glow-ink changes don't always rebuild the chunk under Connector. The
  server sends a block entity update plus a `flattersigns:force_sign_rerender`
  S2C packet (one `BlockPos`), and the client schedules a rerender at that
  position. Same channel and wire format on every version: a raw buffer up
  to 1.20.4, a `CustomPayload` from 1.20.5.

## Consequences

- One jar per Minecraft version, `flattersigns-X.Y.Z+<mc>.jar`, one tag and
  one GitHub release for all of them, one Modrinth version per Minecraft
  version with its own loaders (`tools/modrinth.json`).
- A fix is written once and lands on every version.
- The fallback injects look redundant on Fabric; they are not dead code.
- A new Minecraft version means a `versions/<mc>/gradle.properties`, an
  entry in `settings.gradle.kts` and `tools/modrinth.json`, and rechecking
  every Mojmap and intermediary name under Connector.
