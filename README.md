## Ice And Fire Patcher

This is a mod to fix some critical bugs and crashes in mod
[Ice And Fire](https://www.curseforge.com/minecraft/mc-mods/ice-and-fire-dragons).

## Currently fixed:

- Citadel fetch patreon on main thread cause launching slowness. (Now run on standalone thread)
- Dragon breath will cause severe lag on server.
- Wrong particle render for dragon breath
- The low capability of Title Screen and restore render fading.
- Model Animator won't clear cache after animate cause memory leak.
- Pixie jar color not correct after rejoin world.
- Fire Dragon Bone Sword & Fire Dragon Steel Sword cannot cook drops.

## Enhancements

- Add `iceandfire:pixie_stolen_blacklist` tag.
- Add some golden items into `minecraft:piglin_loved` tag to let piglin pick up them.

**Join our [Discord](https://discord.gg/NDzz2upqAk) to report bugs you want to fix**