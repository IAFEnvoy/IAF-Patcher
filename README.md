## Ice And Fire Patcher

This is a mod to fix some critical bugs and crashes in mod
[Ice And Fire](https://www.curseforge.com/minecraft/mc-mods/ice-and-fire-dragons).

Current maintaining versions: 1.16.5, 1.18.2, 1.19.2.

**1.15.2 & 1.17.1 stopped maintaining due to few players.**

**1.20.1 stopped maintaining, please use [IceAndFire CE](https://www.curseforge.com/minecraft/mc-mods/iceandfire-ce)
Instead.**

## Currently fixed:

- Citadel fetch patreon on main thread cause launching slowness. (Now run on standalone thread)
- Dragon breath will cause severe lag on server.
- Wrong particle render for dragon breath
- The low capability of Title Screen and restore render fading.
- Model Animator won't clear cache after animate cause memory leak.
- Pixie jar color not correct after rejoin world.
- Hippogryphs will fly too high and suck.
- Fire Dragon Bone Sword & Fire Dragon Steel Sword&Tools cannot cook drops.

## Enhancements

- Add `iceandfire:pixie_stolen_blacklist` tag.
- Add some golden items into `minecraft:piglin_loved` tag to let piglin pick up them.

**Join our [Discord](https://discord.gg/NDzz2upqAk) to report bugs you want to fix**