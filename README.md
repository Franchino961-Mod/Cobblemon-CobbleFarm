# 🏭 Cobblemon CobbleFarm

Automate your Pokémon item gathering! A Cobblemon addon that lets you farm Pokémon drops automatically using tier-based production blocks.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![Version](https://img.shields.io/badge/version-0.5.0-blue.svg)]()
[![Fabric](https://img.shields.io/badge/Fabric-0.16.9-blue.svg)](https://fabricmc.net/)
[![Cobblemon](https://img.shields.io/badge/Cobblemon-1.6.0+-red.svg)](https://cobblemon.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![it](https://img.shields.io/badge/lang-it-green.svg)](Docs/README.it.md)

> 📝 **Changelog**: See [CHANGELOG.en.md](Docs/CHANGELOG.en.md) for version history.

---

## 🌟 Why Use CobbleFarm?
- **Passive Loot**: Get Pokémon-specific drops (like wool, leather, or special items) automatically.
- **Upgradeable Systems**: Progress through 5 tiers (Iron to Netherite) for faster production and better durability.
- **Balanced Automation**: Provides a rewarding endgame without breaking the economy thanks to a well-balanced durability system.

---

## 🚀 Quick Start
1. Craft a **CobbleFarm** block (starting with Iron tier) and a **Farm Ball**.
2. Capture a Pokémon using the Farm Ball by right-clicking it.
3. Place an inventory block (like a Chest, Barrel, or Hopper) on the ground.
4. Place the **CobbleFarm** block *directly on top* of the inventory.
5. Insert the captured Farm Ball into the CobbleFarm to start generating drops!

---

## ✨ Main Features
- **5 Production Tiers**: Upgrade your farms from Iron all the way to Netherite. Each tier produces loot faster than the last.
- **Smart Output**: The farm automatically pushes generated drops into the inventory block directly below it. If the inventory is full, the farm pauses automatically to prevent lag and item loss.
- **Durability System**: Nothing lasts forever! Farm Balls have 256 uses. As the farm works, the ball takes damage and will eventually break (losing the Pokémon inside).
- **Dynamic Rendering**: You can visually see the captured Pokémon shrinking and spinning inside the Farm Ball!

---

## ⚙️ Farm Tiers & Durability
The tier of the farm determines both how fast drops are generated and how much damage the Farm Ball takes per cycle.

| Tier | Speed | Ball Damage per Cycle |
|------|-------|-----------------------|
| **Iron** | Very Slow | High (2-3 damage) |
| **Gold** | Slow | Medium (1-2 damage) |
| **Diamond** | Normal | Low (1 damage) |
| **Emerald** | Fast | Very Low (0-1 damage) |
| **Netherite** | Very Fast | **Indestructible** (0 damage) |

*Tip: Upgrading to a Netherite Farm is highly recommended for rare Pokémon, as the Farm Ball will never break!*

---

## 📦 Requirements
- **Minecraft**: 1.21.1
- **Fabric Loader**: 0.16.9 or higher
- **Fabric API**: 0.108.0 or higher
- **Cobblemon**: 1.6.0 or higher

---

## 📥 Installation
1. Install Fabric and the required dependencies.
2. Download the `CobbleFarm` `.jar` file.
3. Place the file in your Minecraft installation's `mods` folder.
4. Launch the game!

---

## 🖥️ Client/Server Behavior
- **Server**: Required. The mod handles block entities and loot generation on the server.
- **Client**: Required. Includes custom 3D models and dynamic rendering for the Farm Ball.

---

## ⚠️ Known Limitations
- The farm requires an inventory block directly beneath it to work. It will not drop items on the ground.
- When a Farm Ball breaks, the Pokémon inside is permanently lost.

---

## 🛠️ Troubleshooting
### The farm is not producing drops!
- Check if there is an inventory block (Chest, Barrel, Hopper) directly below the CobbleFarm.
- Ensure the inventory below is not completely full.
- Verify that the Pokémon you captured actually has a loot table (some Pokémon might not drop anything naturally).

---

## ❓ FAQ
**Q: How do I get my Pokémon back from the Farm Ball?**  
A: Currently, once a Pokémon is captured in a Farm Ball to be used for farming, it is converted into an item and cannot be released back into the world.

**Q: Can I use hoppers to insert Farm Balls?**  
A: The farm is designed for manual insertion of the Farm Ball to prevent accidental losses, but it automatically extracts the produced loot into the block below it.

---

## 💬 Support & Feedback
If you encounter issues or bugs, please report them with:
- Mod version
- Minecraft / Fabric / Cobblemon versions
- Detailed description of the problem
- Crash logs (if applicable)

---

## 📄 License
This mod is licensed under the [MIT License](LICENSE). Feel free to include it in your modpacks!

## 👤 Author
**Franchino961** — [GitHub](https://github.com/Franchino961-Mod)
