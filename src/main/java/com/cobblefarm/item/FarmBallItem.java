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
        super(new Settings().maxCount(16).fireproof());
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PokemonEntity pokemonEntity && !hasPokemon(stack)) {
            if (pokemonEntity.isRemoved() || !pokemonEntity.isAlive()) {
                return ActionResult.PASS;
            }

            Pokemon pokemon = pokemonEntity.getPokemon();

            // Check: Cannot capture other players' Pokemon
            if (pokemon.isPlayerOwned() && (pokemon.getOwnerUUID() == null || !user.getUuid().equals(pokemon.getOwnerUUID()))) {
                if (!user.getWorld().isClient) {
                    user.sendMessage(Text.translatable("message.cobblefarm.cannot_steal_pokemon").formatted(Formatting.RED), true);
                }
                return ActionResult.FAIL;
            }

            // Check: Cannot capture Pokemon currently in battle or busy
            if (pokemonEntity.isBusy()) {
                if (!user.getWorld().isClient) {
                    user.sendMessage(Text.translatable("message.cobblefarm.pokemon_in_battle").formatted(Formatting.RED), true);
                }
                return ActionResult.FAIL;
            }

            if (!user.getWorld().isClient) {
                ItemStack capturedBall = new ItemStack(this);
                NbtCompound data = new NbtCompound();

                // Save full Pokemon data with RegistryManager
                NbtCompound pokemonFullData = new NbtCompound();
                pokemon.saveToNBT(user.getRegistryManager(), pokemonFullData);
                data.put("pokemon_full_data", pokemonFullData);

                // Quick metadata for fast rendering and tooltips
                data.putString("species", pokemon.getSpecies().getResourceIdentifier().toString());
                data.putString("form", pokemon.getForm().getName());
                data.putInt("level", pokemon.getLevel());
                data.putBoolean("shiny", pokemon.getShiny());
                data.putString("displayName", pokemon.getDisplayName(true).getString());
                data.putString("loot_table", pokemon.getSpecies().getResourceIdentifier().toString());

                NbtComponent.set(DataComponentTypes.CUSTOM_DATA, capturedBall, data);

                // Set durability components
                capturedBall.set(DataComponentTypes.MAX_DAMAGE, 256);
                capturedBall.set(DataComponentTypes.DAMAGE, 0);
                capturedBall.set(DataComponentTypes.MAX_STACK_SIZE, 1);

                // Eject held item to prevent losing valuable items (Master Ball, Leftovers, etc.)
                ItemStack held = pokemon.heldItem();
                if (!held.isEmpty()) {
                    ItemStack heldCopy = held.copy();
                    pokemon.swapHeldItem(ItemStack.EMPTY, false, false);
                    if (!user.getInventory().insertStack(heldCopy)) {
                        user.dropItem(heldCopy, false);
                    }
                }

                // Discard entity
                pokemonEntity.discard();

                // Handle item stack in hand vs inventory safely
                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                if (stack.isEmpty()) {
                    user.setStackInHand(hand, capturedBall);
                } else {
                    if (!user.getInventory().insertStack(capturedBall)) {
                        user.dropItem(capturedBall, false);
                    }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static boolean hasPokemon(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) return false;
        return nbtComponent.contains("species");
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
                int level = data.getInt("level");

                tooltip.add(Text.literal("Pokemon: " + name + " (Lv. " + level + ")").formatted(Formatting.GRAY));
                if (shiny) {
                    tooltip.add(Text.literal("✦ Shiny").formatted(Formatting.YELLOW));
                }

                int damage = stack.getOrDefault(DataComponentTypes.DAMAGE, 0);
                int maxDamage = stack.getOrDefault(DataComponentTypes.MAX_DAMAGE, 256);
                int remaining = Math.max(0, maxDamage - damage);
                tooltip.add(Text.translatable("tooltip.cobblefarm.uses_left", remaining, maxDamage).formatted(Formatting.AQUA));
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
