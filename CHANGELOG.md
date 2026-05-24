# PvP Flag — Changelog

## 2.0.2

Update to config offset default because new rendering required different approach with different values. Format is still the same



## 2.0.0

**Complete rewrite as a multiplatform Kotlin mod (Fabric + Forge).**

- Added Forge support alongside Fabric using the Kotlin MultiLoader Template
- Replaced GameRule config with proper config files (Forge Config API Port)
  - `flagLogic`: MUTUAL or PERMISSIVE PvP mode
  - `disableTimeoutSeconds`: configurable disable delay
  - Client config for skull icon position, size, and render distance
- Added timeout-based flag disabling with pulsing skull indicator
  - Flag doesn't disable instantly — starts a countdown (default 5s)
  - PvP damage during countdown cancels it
  - Only the player themselves sees the countdown; others still see the flag as enabled
- Switched networking to badpackets (cross-platform)
- Switched state storage to SavedData (server-authoritative)
