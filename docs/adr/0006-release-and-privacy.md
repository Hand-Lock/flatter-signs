# 0006. Release, versioning and privacy

Date: 2026-09-29
Status: Accepted

## Context

1.0.0 was built and uploaded to Modrinth by hand. Early commits exposed a
personal email.

## Decision

- SemVer tags `vX.Y.Z` on `main`. Patch = fixes, minor = features or
  visible changes, major = removed/renamed config keys or a changed Mode 13h
  contract.
- The jar is `flattersigns-X.Y.Z+<mc>.jar`, the Modrinth version number is
  `X.Y.Z+<mc>` and its name `X.Y.Z`, matching 1.0.0.
- `CHANGELOG.md` in Keep a Changelog format; the release notes come from it.
- `tools/release.sh` does everything: check, build, tag, GitHub release,
  Modrinth version (`tools/modrinth.json`), and sync the Modrinth page body
  from README.md.
- Releases happen only when the user says "release".
- The only committed identity is `HandLock_` with the GitHub noreply email.
  The Modrinth token lives in the macOS Keychain (`modrinth-token`) or
  `$MODRINTH_TOKEN`, never in a file. `tools/check.sh` greps for emails and
  local paths.

## Consequences

- The Modrinth page can't drift from README.md; edit README, not the site.
- History was rewritten once to remove the old email; old SHAs may still be
  cached by GitHub or forks.
