# Grand Teleport

Grand Teleport adds a GTA-style cinematic camera transition to Minecraft
teleports.

When a teleport starts, the camera leaves the player, pulls up through several
zoom stages, moves across the world when possible, and then returns to the
player near the destination. The original teleport is still handled by
Minecraft, the server, or the other mod that started it. Grand Teleport adds
the cinematic transition around it.

## Supported versions

This repository contains version-specific source sets for:

- Minecraft 1.20.1
- Minecraft 1.21.1
- Minecraft 1.21.11
- Minecraft 26.1
- Minecraft 26.1.1
- Minecraft 26.1.2
- Minecraft 26.2

## Main features

- GTA-style pull-out, overhead travel, and pull-in camera transition.
- Configurable zoom-stage camera heights.
- Separate zoom-stage settings for the Overworld, the Nether, and the End.
- Optional player input blocking during the transition.
- Optional cross-dimension camera travel.
- Minecraft sound mode and Grand Teleport custom sound mode.
- ModMenu configuration screen.
- Client commands using both `/gtp` and `/grandtp`.

## Supported teleport sources

Grand Teleport can react to common teleport commands such as:

- `/tp`
- `/teleport`
- `/minecraft:tp`
- `/minecraft:teleport`
- `/execute ... run tp ...`
- `/execute ... run teleport ...`

Depending on the Minecraft version and installed mods, Grand Teleport also
supports several mod or server-assisted teleport flows, including JourneyMap
teleports, Waystones teleports, Warp Plate teleports, command blocks, and other
player-targeted teleports.

For multiplayer server-side teleport sources, Grand Teleport may also need to
be installed on the server.

## Commands

- `/gtp on` or `/grandtp on` - enable the transition.
- `/gtp off` or `/grandtp off` - disable the transition.
- `/gtp status` or `/grandtp status` - show the current status.
- `/gtp player_freeze on` or `/grandtp player_freeze on` - block movement and
  camera input during the transition.
- `/gtp player_freeze off` or `/grandtp player_freeze off` - allow movement
  during the transition.
- `/gtp player_freeze status` or `/grandtp player_freeze status` - show the
  player-freeze status.

Settings are also available from the ModMenu configuration screen.

## Recommended companion mods

Grand Teleport feels best when the camera can see distant terrain during the
transition. Voxy and Distant Horizons are recommended if your PC can run them
comfortably.

These mods are optional. Grand Teleport works without them, but the transition
is more impressive when long-distance terrain is visible.

## Sodium compatibility notes

Sodium compatibility is version-sensitive because Sodium's internal renderer
changes between Minecraft versions.

The following versions have been the most stable in testing:

- Minecraft 1.21.11: Sodium `0.8.0` to `0.8.12`
- Minecraft 26.1.x: Sodium `0.8.9` to `0.8.12`
- Minecraft 26.2: Sodium `0.9.0`

These are tested recommendations, not a guarantee that other versions cannot
work. If the transition flickers or chunks appear abruptly, try changing Sodium
versions or checking Sodium's chunk fade settings.

## Build

```powershell
.\gradlew.bat build
```

Generated jars are written with the Minecraft version and build number, for
example:

- `build/libs/grand-teleport-1.20.1-buildN.jar`
- `build/libs/grand-teleport-1.21.1-buildN.jar`
- `build/libs/grand-teleport-1.21.11-buildN.jar`
- `build/libs/grand-teleport-26.1-buildN.jar`
- `build/libs/grand-teleport-26.1.1-buildN.jar`
- `build/libs/grand-teleport-26.1.2-buildN.jar`
- `build/libs/grand-teleport-26.2-buildN.jar`

## License

Grand Teleport source code and documentation are licensed under the MIT License,
except where otherwise noted.

Bundled Grand Teleport custom sound effects are not licensed under MIT. They are
edited audio assets created from sounds licensed from ZapSplat under a paid
Premium plan, and are included only for use as part of Grand Teleport.

Do not extract, redistribute, sell, sublicense, or reuse the bundled custom
sound files separately.

## Credits

This mod was coded with Codex, and playtesting and supervision were handled by hookuru_.
