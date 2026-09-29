# 0002. Platform: Fabric 1.20.1, Forge via Connector

Date: 2026-09-29
Status: Accepted

## Context

Mode 13h's players are on 1.20.1, on both Fabric and Forge. Maintaining two
loader builds would double the work for a mod this small.

## Decision

- One Fabric mod for Minecraft 1.20.1: Loom, Yarn mappings, Java 17
  bytecode, split `main`/`client` source sets.
- Fabric API is the only dependency.
- Forge is supported through Sinytra Connector + Forgified Fabric API, not
  a separate build. Where Connector remaps or skips a code path, mixins add
  extra injects with `require = 0` against the Mojmap and intermediary names
  (`load`, `handleUpdateTag`, `method_26208`…), so a missing target never
  crashes.
- Glow-ink changes don't always rebuild the chunk on Forge. The server sends
  a block entity update plus a `flattersigns:force_sign_rerender` S2C packet,
  and the client schedules a rerender at that position.

## Consequences

- One jar, uploaded to Modrinth with loaders fabric, forge and neoforge.
- The fallback injects look redundant on Fabric; they are not dead code.
- Porting to a new Minecraft version means rechecking every Mojmap and
  intermediary name.
