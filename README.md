# Hardcore Darkness (Fabric)

Hardcore Darkness recreates the pitch-black nights and fog tuning of the classic True Darkness mod for modern Fabric. For shaders, I suggest tweaking [Complementary](https://modrinth.com/shader/complementary-unbound) as this mod doesn't affect them.

![Fireflies!](https://cdn.modrinth.com/data/cached_images/f92bfc1d52fc4728fb907ddbd0a804473a951403_0.webp)

## Features
- Configurable darkness per dimension plus optional moon phase handling (`config/hardcore_darkness.toml`).
- Adjustable Nether and End fog multipliers to keep those dimensions oppressive.
- Supports [Distant Horizons](https://modrinth.com/mod/distanthorizons)!

## Install
- Requires [Fabric](https://fabricmc.net/) and [Fabric API](https://modrinth.com/mod/fabric-api).
- Optional UI: add [Mod Menu](https://modrinth.com/mod/modmenu) and [Cloth Config](https://modrinth.com/mod/cloth-config) to tweak settings in-game.
- Place the built jar in `mods/` on the client. Servers never need it.

## Build
- Java 21 with Gradle + Fabric Loom.
- Versions live in `gradle.properties`. Run `./gradlew build`.

## License
- CC0-1.0. Use it freely.
