package com.cobblefarm.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CapturedPokemonItem extends Item {

    public static final String NBT_KEY = "cobblefarm:pokemon_data";

    public CapturedPokemonItem() {
        super(new Item.Settings().maxCount(1));
    }

    public static ItemStack create(String species, String form, int level,
                                   boolean shiny, String displayName, String lootTable) {
        ItemStack stack = new ItemStack(CobbleFarmItems.CAPTURED_POKEMON_ITEM);
        NbtCompound data = new NbtCompound();
        data.putString("species", species);
        data.putString("form", form);
        data.putInt("level", level);
        data.putBoolean("shiny", shiny);
        data.putString("displayName", displayName);
        data.putString("loot_table", lootTable);
        
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, data);
        return stack;
    }

    @Nullable
    public static NbtCompound getPokemonData(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        NbtCompound nbt = nbtComponent.copyNbt();
        return nbt.contains(NBT_KEY) ? nbt.getCompound(NBT_KEY) : nbt;
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

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
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
