package com.cobblefarm.item;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.List;

public class FarmBallItem extends Item {
    public FarmBallItem() {
        super(new Settings().maxCount(16));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PokemonEntity pokemonEntity && !hasPokemon(stack)) {
            if (!user.getWorld().isClient) {
                Pokemon pokemon = pokemonEntity.getPokemon();
                
                ItemStack capturedBall = new ItemStack(this);
                NbtCompound data = new NbtCompound();
                data.putString("species", pokemon.getSpecies().getResourceIdentifier().toString());
                data.putString("form", pokemon.getForm().getName());
                data.putInt("level", pokemon.getLevel());
                data.putBoolean("shiny", pokemon.getShiny());
                data.putString("displayName", pokemon.getDisplayName(true).getString());
                data.putString("loot_table", pokemon.getSpecies().getResourceIdentifier().toString());

                NbtComponent.set(DataComponentTypes.CUSTOM_DATA, capturedBall, data);
                
                // Imposta la durabilità e forza lo stack size a 1
                capturedBall.set(DataComponentTypes.MAX_DAMAGE, 256);
                capturedBall.set(DataComponentTypes.DAMAGE, 0);
                capturedBall.set(DataComponentTypes.MAX_STACK_SIZE, 1);

                if (!user.getInventory().insertStack(capturedBall)) {
                    user.dropItem(capturedBall, false);
                }

                pokemonEntity.discard();
                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static boolean hasPokemon(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) return false;
        return nbtComponent.copyNbt().contains("species");
    }

    public static String getSpecies(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        return nbtComponent != null ? nbtComponent.copyNbt().getString("species") : "unknown";
    }

    public static String getForm(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        return nbtComponent != null ? nbtComponent.copyNbt().getString("form") : "";
    }

    public static int getLevel(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        return nbtComponent != null ? nbtComponent.copyNbt().getInt("level") : 1;
    }

    public static boolean isShiny(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        return nbtComponent != null && nbtComponent.copyNbt().getBoolean("shiny");
    }

    public static String getLootTable(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        return nbtComponent != null ? nbtComponent.copyNbt().getString("loot_table") : "";
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        boolean hasPoke = false;
        if (nbtComponent != null) {
            NbtCompound data = nbtComponent.copyNbt();
            if (data.contains("species")) {
                hasPoke = true;
                String name = data.getString("displayName");
                boolean shiny = data.getBoolean("shiny");

                tooltip.add(Text.literal("Pokemon: " + name).formatted(Formatting.GRAY));
                if (shiny) {
                    tooltip.add(Text.literal("✦ Shiny").formatted(Formatting.YELLOW));
                }
                tooltip.add(Text.translatable("tooltip.cobblefarm.captured_pokemon").formatted(Formatting.GOLD));
            }
        }
        if (!hasPoke) {
            tooltip.add(Text.translatable("tooltip.cobblefarm.farm_ball_empty").formatted(Formatting.GRAY));
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            NbtCompound data = nbtComponent.copyNbt();
            if (data.contains("displayName")) {
                return Text.literal("Farm Ball (" + data.getString("displayName") + ")");
            }
        }
        return super.getName(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return hasPokemon(stack);
    }
}
