package com.cobblefarm.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An item representing a Pokémon captured with the Farm Ball.
 *
 * NBT structure stored under "cobblefarm:pokemon_data":
 * {
 *   "species":    "cobblemon:bulbasaur",
 *   "form":       "normal",
 *   "level":      12,
 *   "shiny":      false,
 *   "displayName": "Bulbasaur",
 *   "loot_table": "cobblemon:entities/bulbasaur"
 * }
 */
public class CapturedPokemonItem extends Item {

    public static final String NBT_KEY = "cobblefarm:pokemon_data";

    public CapturedPokemonItem() {
        super(new Item.Settings().maxCount(1));
    }

    // -------------------------------------------------------------------------
    // Static factory — call this from the event handler after a successful catch
    // -------------------------------------------------------------------------

    /**
     * Creates an ItemStack containing the captured Pokémon's data.
     *
     * @param species     ResourceLocation string, e.g. "cobblemon:bulbasaur"
     * @param form        Form name, e.g. "normal"
     * @param level       Pokémon level at time of capture
     * @param shiny       Whether the Pokémon is shiny
     * @param displayName Localised display name of the Pokémon
     * @param lootTable   ResourceLocation of the Cobblemon loot table
     */
    public static ItemStack create(String species, String form, int level,
                                   boolean shiny, String displayName, String lootTable) {
        ItemStack stack = new ItemStack(CobbleFarmItems.CAPTURED_POKEMON_ITEM);
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound data = new NbtCompound();
        data.putString("species", species);
        data.putString("form", form);
        data.putInt("level", level);
        data.putBoolean("shiny", shiny);
        data.putString("displayName", displayName);
        data.putString("loot_table", lootTable);
        nbt.put(NBT_KEY, data);
        return stack;
    }

    // -------------------------------------------------------------------------
    // NBT helpers
    // -------------------------------------------------------------------------

    @Nullable
    public static NbtCompound getPokemonData(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(NBT_KEY)) return null;
        return nbt.getCompound(NBT_KEY);
    }

    public static String getSpecies(ItemStack stack) {
        NbtCompound d = getPokemonData(stack);
        return d != null ? d.getString("species") : "unknown";
    }

    public static String getDisplayName(ItemStack stack) {
        NbtCompound d = getPokemonData(stack);
        return d != null ? d.getString("displayName") : "Unknown Pokémon";
    }

    public static int getLevel(ItemStack stack) {
        NbtCompound d = getPokemonData(stack);
        return d != null ? d.getInt("level") : 1;
    }

    public static boolean isShiny(ItemStack stack) {
        NbtCompound d = getPokemonData(stack);
        return d != null && d.getBoolean("shiny");
    }

    public static String getLootTable(ItemStack stack) {
        NbtCompound d = getPokemonData(stack);
        return d != null ? d.getString("loot_table") : "";
    }

    // -------------------------------------------------------------------------
    // Tooltip
    // -------------------------------------------------------------------------

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world,
                               List<Text> tooltip, TooltipContext context) {
        NbtCompound data = getPokemonData(stack);
        if (data == null) {
            tooltip.add(Text.literal("No data").formatted(Formatting.RED));
            return;
        }

        String name = data.getString("displayName");
        int level = data.getInt("level");
        boolean shiny = data.getBoolean("shiny");

        tooltip.add(Text.literal(name + " (Lv. " + level + ")")
                .formatted(shiny ? Formatting.GOLD : Formatting.AQUA));

        if (shiny) {
            tooltip.add(Text.literal("✦ Shiny").formatted(Formatting.YELLOW));
        }

        tooltip.add(Text.translatable("tooltip.cobblefarm.captured_pokemon")
                .formatted(Formatting.GRAY));
    }

    @Override
    public Text getName(ItemStack stack) {
        NbtCompound data = getPokemonData(stack);
        if (data != null) {
            String name = data.getString("displayName");
            int level = data.getInt("level");
            if (!name.isEmpty()) {
                return Text.literal("Captured " + name + " (Lv. " + level + ")");
            }
        }
        return super.getName(stack);
    }
}
