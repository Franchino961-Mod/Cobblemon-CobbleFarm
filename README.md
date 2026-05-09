# CobbleFarm

A Cobblemon addon for Minecraft 1.21.1 (Fabric) that lets you farm Pokémon drops passively.

## How it works

1. **Craft a Farm Ball** — a special Pokéball that, when used to catch a Pokémon, puts it in your inventory as a `Captured Pokémon` item instead of your party/PC.
2. **Craft a Pokémon Farm** — a tiered block machine (5 tiers: Iron → Gold → Diamond → Emerald → Netherite).
3. **Insert the Captured Pokémon** into the farm's GUI slot.
4. The farm will **passively generate** that Pokémon's drops over time, respecting Cobblemon's loot tables and drop chances.
5. Place a **Chest, Barrel, or any container below** the farm for automatic item output.

## Farm Tiers

| Tier | Ticks/cycle | Speed | Buffer |
|------|-------------|-------|--------|
| Iron | 200 (10s) | 1× | 64 |
| Gold | 140 (~7s) | 1.5× | 128 |
| Diamond | 100 (5s) | 2× | 256 |
| Emerald | 60 (3s) | 3× | 512 |
| Netherite | 30 (1.5s) | 5× | 1024 |

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

## ⚠️ Important: Mixin Configuration

Before the mod works correctly, you **must** configure the Farm Ball capture interception:

1. Decompile the Cobblemon jar (use [Vineflower](https://github.com/Vineflower/vineflower)).
2. Find the method called when a Pokéball capture succeeds (adds Pokémon to party/PC).
3. Check if `CobblemonEvents.POKEMON_CAPTURED` is available (preferred — no mixin needed).
4. If not, update `PokeBallMixin.java` with the real class path and method signature.
   - The mixin file contains a complete implementation template in its Javadoc.

## Project Structure

```
src/main/java/com/cobblefarm/
├── CobbleFarm.java              Main entrypoint
├── CobbleFarmClient.java        Client entrypoint
├── item/
│   ├── FarmBallItem.java        Farm Ball item
│   ├── CapturedPokemonItem.java Captured Pokémon item (NBT-based)
│   └── CobbleFarmItems.java     Item registry
├── block/
│   ├── PokemonFarmBlock.java    Farm block (all tiers)
│   ├── FarmTier.java            Tier enum (speed, buffer, ticks)
│   └── CobbleFarmBlocks.java    Block registry
├── blockentity/
│   ├── PokemonFarmBlockEntity.java  Tick logic, buffer, output
│   └── CobbleFarmBlockEntities.java BlockEntity registry
├── screen/
│   ├── PokemonFarmScreen.java       GUI (client)
│   ├── PokemonFarmScreenHandler.java ScreenHandler
│   └── CobbleFarmScreenHandlers.java Registry
├── network/
│   └── CobbleFarmNetworking.java    Pause toggle packet
├── loot/
│   └── PokemonLootHelper.java       Drop generation from loot tables
└── mixin/
    └── PokeBallMixin.java           Capture interception (configure before use)
```

## Textures

The included textures are **solid-color placeholders**. Replace them with proper pixel art:

- `assets/cobblefarm/textures/block/farm_*.png` — farm block faces (16×16)
- `assets/cobblefarm/textures/item/farm_ball.png` — Farm Ball (16×16)
- `assets/cobblefarm/textures/item/captured_pokemon_item.png` — captured item (16×16)
- `assets/cobblefarm/textures/gui/pokemon_farm.png` — GUI atlas (256×256)

## License

MIT
