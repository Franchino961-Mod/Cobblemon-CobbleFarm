# CobbleFarm

A Cobblemon addon for Minecraft 1.21.1 (Fabric) that lets you farm Pokémon drops passively.

## How it works

1. **Craft a Farm Ball** — a special Pokéball that, when used to catch a Pokémon, puts it in your inventory as a `Captured Pokémon` item instead of your party/PC.
2. **Craft a CobbleFarm** — a tiered block machine (5 tiers: Iron → Gold → Diamond → Emerald → Netherite).
3. **Insert the Captured Pokémon** into the farm's GUI slot.
4. The farm will **passively generate** that Pokémon's drops over time, respecting Cobblemon's loot tables and drop chances.
5. Place a **Chest, Barrel, or any container below** the farm for automatic item output (internal buffers have been removed).

## Farm Tiers

| Tier | Ticks/cycle | Speed |
|------|-------------|-------|
| Iron | 200 (10s) | 1× |
| Gold | 140 (~7s) | 1.5× |
| Diamond | 100 (5s) | 2× |
| Emerald | 60 (3s) | 3× |
| Netherite | 30 (1.5s) | 5× |

Recipes are **upgrade-progressive**: each tier wraps the previous one.

## Setup & Build

### Prerequisites
- JDK 21+
- Internet access (to download Gradle, Fabric API, Cobblemon via Maven)

### Steps
```bash
# 1. Clone / unzip the project
cd cobblefarm

# 2. Build
./gradlew build

# 3. Find the JAR in build/libs/cobblefarm-1.0.0.jar
```

## Project Structure

```
src/main/java/com/cobblefarm/
├── CobbleFarm.java              Main entrypoint (handles capture events)
├── CobbleFarmClient.java        Client entrypoint (handles UI registration)
├── item/
│   ├── FarmBallItem.java        Farm Ball item
│   ├── CapturedPokemonItem.java Captured Pokémon item (NBT-based)
│   └── CobbleFarmItems.java     Item registry
├── block/
│   ├── CobbleFarmBlock.java     Farm block (all tiers)
│   ├── FarmTier.java            Tier enum (speed, ticks)
│   └── CobbleFarmBlocks.java    Block registry
├── blockentity/
│   ├── CobbleFarmBlockEntity.java  Tick logic, external output, rendering sync
│   └── CobbleFarmBlockEntities.java BlockEntity registry
├── screen/
│   ├── CobbleFarmScreen.java       GUI & Entity Rendering (client)
│   ├── CobbleFarmScreenHandler.java ScreenHandler
│   └── CobbleFarmScreenHandlers.java Registry
├── network/
│   └── CobbleFarmNetworking.java    Pause toggle packet
└── loot/
    └── PokemonLootHelper.java       Drop generation from loot tables
```

## Textures & Models

- `assets/cobblefarm/textures/block/` — CobbleFarm block faces
- `assets/cobblefarm/textures/item/` — Farm Ball and Captured Pokémon icons
- `assets/cobblefarm/textures/gui/cobblefarm.png` — GUI background
- `assets/cobblefarm/models/block/` — Block models (transparent glass style)

## License

MIT

