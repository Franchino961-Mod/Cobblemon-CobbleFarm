# Changelog - CobbleFarm

All notable changes to the **CobbleFarm** mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.6.0] - 2026-10-15

### Fixed
- **Storage Mod Compatibility (Sophisticated Storage, Drawers, etc.)**: Upgraded container output handling to use Fabric Transfer API (`ItemStorage.SIDED`) with seamless fallback to vanilla inventories. CobbleFarm blocks now directly recognize and output drops into Sophisticated Storage barrels/chests, Storage Drawers, and modded inventories.
- **GUI Synchronization & Pause Toggle**: Migrated the screen handler to Fabric's `ExtendedScreenHandlerType` synchronizing the `BlockPos` packet codec to client screens. Fixed the Pause toggle button and resolved GUI desync/crash issues.
- **Entity Rendering Optimization in GUI**: Optimized Pokémon rendering in the CobbleFarm screen using a specialized state key (`cachedPokemonKey`) to avoid redundant entity re-instantiation and freezing when item durability updates.
- **Loot Generation NPE Guard**: Provided a valid Pokémon context in `PokemonLootHelper` to prevent NullPointerExceptions during Cobblemon conditional drop calculations.

### Added
- **Internal Output Buffer**: Added an internal output buffer in `CobbleFarmBlockEntity` to safely store drops when the attached container below is full or temporarily unavailable, preventing item loss.
- **Farm Ball Safety & Protections**: Prevented capturing other players' Pokémon, NPC Pokémon, or Pokémon engaged in battle with localized feedback messages.
- **Held Item Protection**: Captured Pokémon now safely eject any held items (such as Leftovers, Master Balls, etc.) into the player's inventory or ground instead of destroying them.
- **Full NBT Preservation**: Pokémon data inside Farm Balls is now saved using Cobblemon's `saveToNBT` with `RegistryManager` for complete IV, EV, and component preservation.
- **Smithing Table Netherite Recipe**: Upgraded the Netherite Farm recipe to standard Minecraft 1.21 Smithing Template transform (`minecraft:smithing_transform`).
- **Smooth World 3D Animation**: Added smooth continuous 360° rotation for the Pokémon rendered inside the CobbleFarm block.
- **Remaining Uses Tooltip**: Added a uses remaining counter to Farm Ball tooltips.
- **Automated Publishing Workflow**: Added `mod-publish-plugin` and GitHub Actions CI workflow for automated releases to CurseForge and GitHub.

---

## [0.5.1] - 2026-05-26
### Added
- **Localization**: Added translation files for German, Spanish, French, Portuguese, Russian, and Chinese languages.
- **CurseForge Badge**: Added a CurseForge download badge to the project README.
- **Compatibility**: Added a compatibility & modpack support section to the README.

### Changed
- **GUI Texture**: Updated the background texture for the CobbleFarm GUI screen.
- **Project Structure**: Updated gitignore patterns and configured gitattributes for line ending normalization.

## [0.5.0] - 2026-05-22
### Added
- **Dynamic Tooltips**: Added empty and captured state tooltips for the Farm Ball item.
- **Block Mining Tags**: Added `minecraft:mineable/pickaxe` tag for CobbleFarm blocks, along with specific tool tier requirements:
  - Stone tool required for Iron Farm.
  - Iron tool required for Gold, Diamond, and Emerald Farms.
  - Diamond tool required for Netherite Farm.
- **Block Drop Loot Tables**: Added block loot tables so farm blocks drop themselves when mined with the appropriate tool.
- **Mod Icon Asset**: Included high-quality mod icon (`logo.png`).

### Fixed
- **Minecraft 1.21 Recipe Formats**: Updated and moved recipes to the new 1.21 `recipe` folder structure, formatting results using the `"id"` field instead of `"item"`.

## [0.4.0] - 2026-05-20
### Added
- **Durability System**: Farm Balls now have a maximum durability of 256 uses and receive damage per production cycle based on the farm tier (higher tiers have lower damage chance).
- **Dynamic 3D Block Entity Renderer**: Implemented `CobbleFarmRenderer` to render the captured Pokémon dynamically inside the farm block in-world.
- **Centralized Base Model**: Introduced `cobblefarm_preset` block model as a centralized parent for all farm tier models.
- **CobbleFarm Preset Texture**: Added a shared base texture for the preset block model.

### Changed
- **Model Restructuring**: Updated block and item models of all tiers to inherit from `cobblefarm_preset`.
- **Project Structure**: Cleaned up project build files, reorganized `gradle.properties`, and cleaned up `.gitignore`.

### Fixed
- **Deterministic Loot Seeding**: Replaced static Random instance with `world.getRandom()` in `PokemonLootHelper`.
- **Screen Entity Render Bounds**: Corrected Pokémon entity rendering bounds and status text position in the CobbleFarm screen.
- **Inventory Slot Logic**: Prevented inserting a second Pokémon into an already occupied Farm Ball slot in the screen handler.
- **Block Tier Codec**: Fixed block registration to use an instance-level codec, ensuring each farm block retains its tier properties.

## [0.3.0] - 2026-05-18
### Added
- **New Asset Directory Structure**: Reorganized and added textures (`cobblefarm.png`, `mycelium.png`, `glass.png`).
- **Clean Preset Models**: Introduced updated `farm_preset.json` and `farm_ball.json` models.

### Changed
- **Namespace and Asset Renaming**: Renamed all block and item model files from `pokemon_farm_*` prefix to `cobblefarm_*` prefix.

### Removed
- **Unused Assets**: Removed legacy asset files including `pokemon_farm.png`, old block-tier textures (`farm_iron.png`, `farm_gold.png`, etc.), `captured_pokemon_item.png`, `farm_ball_model.json`, and legacy mixin configs.

## [0.2.0] - 2026-05-15
### Changed
- **Yarn Mapping Migration**: Migrated the entire codebase (blocks, items, block entities, network packets, loot helpers, mixins, and GUI screens) to Yarn mappings for improved development compatibility.
- **Namespace Cleanups**: Renamed core classes and resources from the `PokemonFarm` prefix to the `CobbleFarm` namespace (e.g., `PokemonFarmBlockEntity` -> `CobbleFarmBlockEntity`).
- **Client Separation**: Moved client-only logic (like screen rendering and the client entrypoint) into a dedicated `src/client` source directory.

## [0.1.0] - 2026-05-10
### Added
- **Initial Mod Architecture**: Created basic Fabric mod setup, gradle build files, and manifest metadata.
- **5 Farm Tiers**: Added Iron, Gold, Diamond, Emerald, and Netherite block variants with configurable production speeds.
- **Farm Ball Item**: Implemented item used to capture Pokémon and insert them into farm blocks.
- **Loot Table Integrations**: Passive drops calculated and generated dynamically based on the captured Pokémon's vanilla or Cobblemon loot table.
- **Capture Hook**: Added mixin hook to capture capture events.
- **Interactive UI**: Screen handler and GUI textures allowing players to view capture status, inserted Pokémon, and access inventory.
- **Smart Output**: Automatically pushes generated items to container blocks directly beneath the farm block.
- **Initial Recipes**: Crafting recipes for all farm tiers and the Farm Ball.
- **Localization**: Added English translation keys (`en_us.json`).