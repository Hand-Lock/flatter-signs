#!/usr/bin/env python3
# Writes the vanilla sign blockstates and our flat models. Run from the repo
# root and commit the output; tools/check.sh fails if they drift.
import json
import os

WOODS = [
    "oak", "spruce", "birch", "jungle", "acacia",
    "dark_oak", "mangrove", "cherry", "bamboo", "crimson", "warped",
]

T = 0.05  # sheet thickness: close to flat, but no z-fighting
CROP = 11  # default wall_sign_texture_crop_height; the client recrops on load
WALL_Y = (4.0, 15.0)  # bottom edge sits on the wall-sign outline (y = 4)

# facing (the way the front looks) -> sheet flush with the opposite face
WALL = {
    "south": ([0, WALL_Y[0], 0.0], [16, WALL_Y[1], T], ("south", "north")),
    "west": ([16.0 - T, WALL_Y[0], 0], [16.0, WALL_Y[1], 16], ("east", "west")),
    "north": ([0, WALL_Y[0], 16.0 - T], [16, WALL_Y[1], 16.0], ("south", "north")),
    "east": ([0.0, WALL_Y[0], 0], [T, WALL_Y[1], 16], ("east", "west")),
}
# wall-hanging plate axis -> centered sheet, raised 1px to meet its bracket
PLATE = {
    "x": ([0.0, 1.0, 8.0 - T / 2], [16.0, 17.0, 8.0 + T / 2], ("south", "north")),
    "z": ([8.0 - T / 2, 1.0, 0.0], [8.0 + T / 2, 17.0, 16.0], ("east", "west")),
}


def quad(frm, to, faces, v2=16, **extra):
    return {"from": frm, "to": to, **extra, "shade": False,
            "faces": {f: {"uv": [0, 0, 16, v2], "texture": "#tex"} for f in faces}}


def model(tex, elements):
    return {"ambientocclusion": False, "render_type": "minecraft:cutout",
            "textures": {"tex": tex, "particle": tex}, "elements": elements}


def cross(tex, y0, y1):
    rot = lambda a: {"origin": [8, 8, 8], "axis": "y", "angle": a, "rescale": False}
    return model(tex, [quad([0, y0, 8], [16, y1, 8], ("north", "south"), rotation=rot(a))
                       for a in (45, -45)])


def write(path, data):
    path = f"src/main/resources/assets/{path}.json"
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(data, f, indent=2)


def blockstate(name, variants):
    write(f"minecraft/blockstates/{name}", {"variants": {k: {"model": v} for k, v in variants.items()}})


for w in WOODS:
    sign, hanging = f"minecraft:item/{w}_sign", f"minecraft:item/{w}_hanging_sign"
    m = f"flattersigns:block/{w}"

    write(f"flattersigns/models/block/{w}_sign_flat", cross(sign, 0.0, 16.0))
    blockstate(f"{w}_sign", {f"rotation={i}": f"{m}_sign_flat" for i in range(16)})

    for facing, (frm, to, faces) in WALL.items():
        write(f"flattersigns/models/block/{w}_wall_sign_flat_{facing}", model(sign, [quad(frm, to, faces, CROP)]))
    blockstate(f"{w}_wall_sign", {f"facing={f}": f"{m}_wall_sign_flat_{f}" for f in WALL})

    # Raised 1px so it meets the ceiling.
    write(f"flattersigns/models/block/{w}_hanging_sign_flat", cross(hanging, 1.0, 17.0))
    blockstate(f"{w}_hanging_sign", {f"rotation={i},attached={a}": f"{m}_hanging_sign_flat"
                                     for i in range(16) for a in ("true", "false")})

    for axis, (frm, to, faces) in PLATE.items():
        write(f"flattersigns/models/block/{w}_wall_hanging_sign_flat_{axis}", model(hanging, [quad(frm, to, faces)]))
    blockstate(f"{w}_wall_hanging_sign", {f"facing={f}": f"{m}_wall_hanging_sign_flat_{'x' if f in ('north', 'south') else 'z'}"
                                          for f in ("south", "north", "west", "east")})
