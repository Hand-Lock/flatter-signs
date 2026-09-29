#!/bin/sh
# Offline checks: generated assets in sync, mixin registry, Gradle build, refmaps,
# scan for private data. Exits non-zero on any failure.
# Usage: tools/check.sh
set -u

cd "$(dirname "$0")/.." || exit 1
TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT
fail=0
err() { printf 'FAIL %s\n' "$*"; fail=1; }

command -v jq >/dev/null || { echo "jq missing: brew install jq"; exit 1; }

# Generated assets: run the generator into a scratch tree and compare, so
# hand edits and stale files both show up.
R=src/main/resources
mkdir -p "$TMP/gen"
(cd "$TMP/gen" && python3 "$OLDPWD/tools/gen_flat_sign_assets.py" >/dev/null) ||
    err "tools/gen_flat_sign_assets.py failed"
for d in assets/minecraft/blockstates assets/flattersigns/models/block; do
    diff -rq "$TMP/gen/$R/$d" "$R/$d" >/dev/null ||
        err "$R/$d differs from the generator (run python3 tools/gen_flat_sign_assets.py)"
done

# Mixin registry: each source set's mixins are listed exactly once, in its
# own config, and every listed entry exists.
registry() { # SOURCESET CONFIG
    P=src/$1/java/com/handlock_/flattersigns/mixin
    [ -d "$P" ] && (cd "$P" && find . -name '*.java') | sed 's|^\./||; s|\.java$||; s|/|.|g' | sort > "$TMP/have"
    jq -r '((.mixins // []) + (.client // []) + (.server // []))[]' "src/$1/resources/$2" | sort > "$TMP/listed"
    for m in $(uniq -d "$TMP/listed"); do err "$2: $m listed twice"; done
    for m in $(comm -23 "$TMP/have" "$TMP/listed"); do err "$2: $m not listed"; done
    for m in $(comm -13 "$TMP/have" "$TMP/listed"); do err "$2: $m listed but missing"; done
}
registry main flattersigns.mixins.json
registry client flattersigns.client.mixins.json

# Build: compiles, runs the mixin annotation processor, writes the refmap.
out=$(./gradlew build -q 2>&1) || { err "gradle build:"; printf '%s\n' "$out" | tail -40 | sed 's/^/    /'; }

# Refmaps: a config naming the wrong one still loads, but its named injects
# silently miss in production (require = 0).
jar=$(ls -t build/libs/*.jar 2>/dev/null | grep -v -- '-sources' | head -1)
for c in src/main/resources/flattersigns.mixins.json src/client/resources/flattersigns.client.mixins.json; do
    r=$(jq -r .refmap "$c")
    unzip -l "$jar" "$r" >/dev/null 2>&1 || err "$c: refmap $r is not in the jar"
done

# Private data: emails other than GitHub noreply, local home paths.
priv=$(git ls-files -co --exclude-standard | grep -v '^tools/check\.sh$' | while IFS= read -r f; do
    [ -f "$f" ] || continue
    grep -HnoIE '[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}|/Users/[A-Za-z0-9._-]+|/home/[a-z][A-Za-z0-9._-]*' "$f"
done | grep -vE ':[0-9]+:([^:]*@users\.noreply\.github\.com|noreply@anthropic\.com)$')
[ -n "$priv" ] && { err "private data:"; printf '%s\n' "$priv" | sed 's/^/    /'; }

[ "$fail" = 0 ] && echo "check: ok"
exit "$fail"
