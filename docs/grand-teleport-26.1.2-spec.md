# Grand Teleport 26.1.2 Specification

This document records the 26.1.2 implementation as the canonical behavior to
port to other Minecraft versions. Version-specific changes should stay as small
as possible and should preserve the behavior below.

## Mod Identity

- Display name: `Grand Teleport`
- Archive base name: `grand-teleport`
- Mod id and asset namespace: `gtalike_teleport`
- Config file: `grand_teleport.properties`
- Legacy config migration: if `gtalike_teleport.properties` exists and the new
  file does not, the legacy file is copied to `grand_teleport.properties`.

## Client Commands

- Register both `/gtp` and `/grandtp`.
- Supported arguments:
  - `on`, `off`, `status`
  - `player_freeze on`, `player_freeze off`, `player_freeze status`
- `/gtatp` is no longer the public command entry point.

## Teleport Transition

- The 26.1.2 flow is:
  1. Camera exits the player body.
  2. Start-side zoom stages play.
  3. Optional overhead camera travel runs.
  4. End-side zoom stages play.
  5. Camera enters the player body.
- Body camera height default: `6.0` blocks.
- Body glide height default: `0.5` blocks.
- Body glide ticks default: `10`.
- Player model and shadow hiding default: `2` ticks at body exit/entry.
- Zoom stage heights are dimension-specific for Overworld, Nether, and End.
  Defaults for each dimension are `50 / 100 / 150`.
- Zoom stage height range: `8.0` to `512.0` blocks.
- Zoom stage ticks default: `13 / 13 / 13` for zoom-out and zoom-in.
- Zoom stage tick range: `1` to `200`.
- Zoom stage glide height default: `0.5` blocks, range `0.1` to `5.0`.
- Zoom stage glide ticks default: `13`.
- Overhead travel defaults:
  - default duration: `40` ticks
  - minimum duration: `20` ticks
  - maximum duration: `60` ticks
  - speed basis: `30` horizontal blocks per tick
  - pre-travel wait: `10` ticks
  - pre-push wait: `10` ticks
- Cross-dimension travel is disabled by default. When disabled, the transition
  skips overhead camera travel between dimensions and uses `20` tick waits around
  the real teleport.
- During the transition, the game menu should not be opened.
- If the local player dies during a transition, the transition is cancelled and
  the camera is moved to the death location instead of continuing the normal path.

## Sounds

- The Sounds tab supports two sources:
  - `Minecraft`
  - `Grand Teleport`
- Minecraft sound volume default: `1.0`.
- Grand Teleport custom sound volume default: `0.3`.
- Both volume sliders are clamped to `0.1` through `1.0` and must change volume,
  not pitch.
- Custom sound timing:
  - player body exit: `camera_out.ogg`
  - zoom-out stages 1 and 2: `zoom_out_short.ogg`
  - zoom-out stage 3: `zoom_out_long.ogg`
  - overhead travel: `teleport.ogg`
  - zoom-in stages 1 and 2: `zoom_in_short.ogg`
  - zoom-in stage 3: `zoom_in_long.ogg`
  - player body entry: `camera_in.ogg`
- Custom travel sound fades out over `30` ticks when the destination overhead
  point is reached.

## Config Screen

- ModMenu opens the config screen.
- The first page shown is `General`.
- Tabs:
  - `General`
  - `Zoom Stage`
  - `Zoom Stage 2`
  - `Sounds`
  - `Others`
- `Zoom Stage` contains dimension-specific height sliders. The dimension buttons
  use vanilla block textures:
  - Overworld: `assets/minecraft/textures/block/grass_block_side.png`
  - Nether: `assets/minecraft/textures/block/netherrack.png`
  - End: `assets/minecraft/textures/block/end_stone.png`
- `Zoom Stage 2` contains zoom stage tick fields.
- `General` contains effect, player freeze, and cross-dimension camera travel
  toggles.
- `Others` contains Warp Plate and external teleport transition toggles.
- The layout editor exists but is hidden by default:
  `configLayoutEditorButtonVisible=false`.
- Layout editor data is stored in normalized config values so the screen keeps
  the same proportions when the window size changes.

## Teleport Sources

- Client-detected teleports include direct `/tp` and `/teleport` commands,
  `execute ... run tp`, JourneyMap command packets, and compatible Waystones UI
  flows where the client can infer the target.
- Server-assisted teleports include Warp Plate and externally initiated player
  teleports such as command blocks or another player targeting the local player.
- On multiplayer servers, server-assisted transitions require Grand Teleport on
  the server. If the server side is absent, those server-only sources cannot be
  detected reliably by the client.

## Render Compatibility

- Vanilla/Sodium chunk mask fade is a client-side visual mask and must not be
  confused with Sodium's own chunk fade duration.
- Distant Horizons enabled rendering should not be hidden by the mask fade, and
  normal chunks should be kept visible while testing DH behavior.
- Bobby enabled rendering should keep normal chunks visible to avoid flickering
  caused by a zero or near-zero Sodium chunk fade duration.
- Sodium chunk fade should not be forced to `none` or a very small value during
  Grand Teleport transitions.

## Porting Notes

- Keep `src/mc26_1_2` as the canonical implementation.
- 26.1, 26.1.1, and 26.2 should stay closest to 26.1.2 and should ideally need
  only mapping or loader-adjacent fixes.
- 1.21.11 uses the default `src/main` source set in this project.
- 1.21.1 and 1.20.1 may need API adaptations for client UI, resource identifiers,
  networking payloads, and sound or render method names.
- Prefer a compile-driven port: copy 26.1.2 first, then make the smallest
  version-local changes needed to compile and preserve behavior.
