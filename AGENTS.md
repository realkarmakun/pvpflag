# PvPFlag Mod — Agent Instructions

## Project Structure

Multiplatform Minecraft mod (Fabric + Forge) for MC 1.20.1 using the Kotlin MultiLoader Template.
All game logic lives in `common/`; `fabric/` and `forge/` are thin platform glue.

- **`common/`** — Kotlin + Java sources, mixins, networking, config, rendering, platform service interfaces
- **`fabric/`** — Fabric entrypoints (`PvpFlagFabric.kt`, `PvpFlagFabricClient.kt`), platform service impls
- **`forge/`** — `@Mod` class (`PvpFlagForge.kt`), `ClientSetup.kt` (loaded via `runWhenOn`), platform service impls
- **`buildSrc/`** — shared Gradle conventions (`multiloader-common.gradle`, `multiloader-loader.gradle`)

## Build & Run

```bash
./gradlew build                          # build everything
./gradlew :fabric:build                  # fabric only
./gradlew :forge:build                   # forge only
./gradlew compileKotlin compileJava      # quick compile check (no jar)
```

No tests exist yet. Build success = verification.

## Key Architecture Decisions

- **Package**: `io.github.realkarmakun.pvpflag`, Mod ID: `pvpflag`
- **Networking**: `badpackets` library (cross-platform), NOT platform-specific packet APIs
- **State**: `SavedData` on overworld (server-authoritative), NOT Cardinal Components
- **Config**: Forge Config API Port (`fcap`) on both platforms — server common config + client config
- **Forge uses KotlinForForge (KFF)**: `modLoader = "kotlinforforge"` in mods.toml, uses `MOD_BUS` / `runWhenOn` from `thedarkcolour.kotlinforforge.forge`, NOT `FMLJavaModLoadingContext`
- **Forge client code**: must be in a separate class loaded via `runWhenOn(Dist.CLIENT)` — never import client classes in `PvpFlagForge` directly, or dedicated server crashes (`RuntimeDistCleaner`)

## Common Pitfalls

- **`@JvmField`** is required on Kotlin `object` vals accessed from Java (mixins). Without it: "has private access" compile error.
- **`@Mod.EventBusSubscriber`** on Kotlin objects still gets loaded on dedicated server — use `runWhenOn` instead for Forge client code.
- **Fabric Loom**: `uploadFile` must be `tasks.remapJar`, not `tasks.jar`.
- **Forge moddev plugin**: dependency version strings need `.get()` suffix (e.g., `libs.versions.badpackets.get()`).
- **Minecraft mappings**: use `writeUUID`/`readUUID` (capital D), not `writeUuid`/`readUuid`.
- **`graphics.blit()` float overload**: u/v offset params must be `0.0f`, not `0` (int overload has different signature).
- **Kotlin `object` init blocks** run at class load time — any client-only code in them will crash dedicated server.

## Config Files

- `pvpflag-server.toml` — server-side: `flagLogic` (MUTUAL/PERMISSIVE), `disableTimeoutSeconds`
- `pvpflag-client.toml` — client-side: HUD skull position, above-player skull position/size/distance
- `libs.versions.toml` — version catalog (NOT in `gradle/`, it's at project root)
- `gradle.properties` — mod metadata, version (`version=2.0.0+1.20.1`), Modrinth settings

## Publishing to Modrinth

```bash
MODRINTH_TOKEN=xxx MODRINTH_DEBUG=false ./gradlew :fabric:modrinth   # publish fabric
MODRINTH_TOKEN=xxx MODRINTH_DEBUG=false ./gradlew :forge:modrinth    # publish forge
```

- `MODRINTH_DEBUG` defaults to `true` (dry run) if unset. Set to `false` to actually upload.
- CI workflow: `.github/workflows/publish.yml` triggers on tag push.
- Modrinth project slug: `pvpflag`
- Fabric deps: `fabric-api`, `fabric-language-kotlin`, `badpackets`, `forge-config-api-port`
- Forge deps: `badpackets`, `kotlin-for-forge`

## Flag State System

Three states: ENABLED → DISABLING (timeout countdown) → DISABLED.
- DISABLING is private: only the player themselves sees the pulsing countdown; other players still see ENABLED.
- PvP damage during DISABLING resets to ENABLED.
- Server ticks timeouts, client counts down locally from server-sent remaining time.
- `PvpFlagState.tickServer()` called from platform server tick events.
- `PvpFlagClientState.tick()` called from platform client tick events.
