## Ice And Fire Patcher

This is a mod to fix some critical bugs and crashes in mod
[Ice And Fire](https://www.curseforge.com/minecraft/mc-mods/ice-and-fire-dragons).

Current maintaining versions: 1.16.5, 1.18.2, 1.19.2, 1.20.1.

**1.15.2 & 1.17.1 stopped maintaining due to few players.**

## Currently fixed:

- Citadel fetch patreon on main thread cause launching slowness. (Now run on standalone thread)
- Dragon breath will cause severe lag on server.
- Wrong particle render for dragon breath
- The low capability of Title Screen and restore render fading.
- Model Animator won't clear cache after animate cause memory leak.
- Pixie jar color not correct after rejoin world.
- Hippogryphs will fly too high and suck.
- Fire Dragon Bone Sword & Fire Dragon Steel Sword&Tools cannot cook drops.
- (1.20.1) Gorgon will break all when it attacks player.
- (1.18.2–1.20.1) AI searches can miss every refresh tick and stop finding food or targets.
- Dragon mating uses the wrong continuation method; sheep following and pixie fleeing stop too early.
- The look goal has an uninitialized observer and incorrect rider filtering.
- Invalid or stale Hydra head indices can cause an out-of-bounds access when hurt.
- Slow multipart entities perform the same collision query twice per tick.
- Cyclops tree-breaking slowdown discards the updated movement vector.

See the [simple patch report and test instructions](docs/simple-patches-2026-11-12.md) for version coverage and validation limits.

## Enhancements

- Add `iceandfire:pixie_stolen_blacklist` tag.
- Add some golden items into `minecraft:piglin_loved` tag to let piglin pick up them.

**Join our [Discord](https://discord.gg/NDzz2upqAk) to report bugs you want to fix**
