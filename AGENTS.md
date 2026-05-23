# PvPFlag Mod — Agent Instructions

## Project Structure

Multiplatform Minecraft mod (Fabric + NeoForge) for MC 1.21.1 using the Kotlin MultiLoader Template.
All game logic lives in `common/`; `fabric/` and `neoforge/` are thin platform glue.

- **`common/`** — Kotlin + Java sources, mixins, networking, config, rendering, platform service interfaces
- **`fabric/`** — Fabric entrypoints (`PvpFlagFabric.kt`, `PvpFlagFabricClient.kt`), platform service impls
- **`neoforge/`** — `@Mod` class (`PvpFlagNeoForge.kt`), `PvpFlagNeoForgeClient.kt` (client events via `@EventBusSubscriber(value = [Dist.CLIENT])`), platform service impls
- **`buildSrc/`** — shared Gradle conventions (`multiloader-common.gradle`, `multiloader-loader.gradle`)

## Build & Run

```bash
./gradlew build                          # build everything
./gradlew :fabric:build                  # fabric only
./gradlew :neoforge:build                # neoforge only
./gradlew compileKotlin compileJava      # quick compile check (no jar)
```

No tests exist yet. Build success = verification.

## Key Architecture Decisions

- **Package**: `io.github.realkarmakun.pvpflag`, Mod ID: `pvpflag`
- **Mappings**: 100% Mojmap (official). `ResourceLocation` (not `Identifier`).
- **Networking**: `badpackets` 0.8.x (cross-platform) with **typed payloads** (`CustomPacketPayload` + `StreamCodec`), NOT platform-specific packet APIs
  - `common()` registers both server and client channels + server receivers + server ready callback
  - `client()` registers client receivers + client ready callback
  - All channel types must be registered in `common()` so both sides know about all payload types
- **State**: `SavedData` on overworld (server-authoritative), using `SavedData.Factory` with `CompoundTag` serialization. `save()` takes `(CompoundTag, HolderLookup.Provider)`.
- **Config**: Forge Config API Port (`fcap`) on both platforms — uses `net.neoforged.neoforge.common.ModConfigSpec` (NeoForge API). Fabric registers via `NeoForgeConfigRegistry` from fcap.
- **NeoForge uses KotlinForForge (KFF)**: `modLoader = "kotlinforforge"` in mods.toml, uses `LOADING_CONTEXT` from `thedarkcolour.kotlinforforge.neoforge.forge`
- **NeoForge client code**: separate `PvpFlagNeoForgeClient` object with `@EventBusSubscriber(value = [Dist.CLIENT])` — never import client classes in `PvpFlagNeoForge` directly, or dedicated server crashes

## Common Pitfalls

- **`@JvmField`** is required on Kotlin `object` vals accessed from Java (mixins). Without it: "has private access" compile error.
- **Fabric Loom**: `uploadFile` must be `tasks.remapJar`, not `tasks.jar`.
- **Minecraft mappings**: use `writeUUID`/`readUUID` (capital D), not `writeUuid`/`readUuid`.
- **`graphics.blit()` float overload**: u/v offset params must be `0.0f`, not `0` (int overload has different signature).
- **Kotlin `object` init blocks** run at class load time — any client-only code in them will crash dedicated server.
- **badpackets 0.8.x**: must use typed payloads (`CustomPacketPayload` subclasses with `StreamCodec`), NOT untyped `ResourceLocation`-based channels. Untyped payloads cause `ClassCastException: UntypedPayload cannot be cast to DiscardedPayload` on NeoForge.
- **Vertex API (1.21.1)**: `.vertex().uv().color().endVertex()` chain is gone. Use `.addVertex(matrix, x, y, z).setUv(u, v).setColor(r, g, b, a)` with no `endVertex()`. `Tesselator.getInstance().begin()` returns `BufferBuilder` directly (no `.builder`). `bufferBuilder.build()` replaces `.end()`.
- **`client.frameTime`** is gone in 1.21.1 — use `DeltaTracker.getGameTimeDeltaPartialTick(false)` or `0f`.
- **`HudRenderCallback`** (Fabric) takes `(GuiGraphics, DeltaTracker)` instead of `(GuiGraphics, float)`.
- **NeoForge GUI layers**: `RegisterGuiLayersEvent` uses `LayeredDraw.Layer` which takes `(GuiGraphics, DeltaTracker)`.
- **NeoForge imports**: all `net.minecraftforge.*` → `net.neoforged.*` (e.g. `net.neoforged.neoforge.common.NeoForge` instead of `net.minecraftforge.common.MinecraftForge`).

## Config Files

- `pvpflag-server.toml` — server-side: `flagLogic` (MUTUAL/PERMISSIVE), `disableTimeoutSeconds`
- `pvpflag-client.toml` — client-side: HUD skull position, above-player skull position/size/distance
- `libs.versions.toml` — version catalog (NOT in `gradle/`, it's at project root)
- `gradle.properties` — mod metadata, version (`version=2.0.0+1.21.1`), Modrinth settings

## Publishing to Modrinth

```bash
MODRINTH_TOKEN=xxx MODRINTH_DEBUG=false ./gradlew :fabric:modrinth   # publish fabric
MODRINTH_TOKEN=xxx MODRINTH_DEBUG=false ./gradlew :neoforge:modrinth  # publish neoforge
```

- `MODRINTH_DEBUG` defaults to `true` (dry run) if unset. Set to `false` to actually upload.
- CI workflow: `.github/workflows/publish.yml` triggers on tag push.
- Modrinth project slug: `pvpflag`
- Fabric deps: `fabric-api`, `fabric-language-kotlin`, `badpackets`, `forge-config-api-port`
- NeoForge deps: `badpackets`, `kotlin-for-forge`

## Flag State System

Three states: ENABLED → DISABLING (timeout countdown) → DISABLED.
- DISABLING is private: only the player themselves sees the pulsing countdown; other players still see ENABLED.
- PvP damage during DISABLING resets to ENABLED.
- Server ticks timeouts, client counts down locally from server-sent remaining time.
- `PvpFlagState.tickServer()` called from platform server tick events.
- `PvpFlagClientState.tick()` called from platform client tick events.
