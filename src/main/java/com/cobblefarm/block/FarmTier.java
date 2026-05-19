package com.cobblefarm.block;

import net.minecraft.util.math.random.Random;

/**
 * Defines the five tiers of CobbleFarm blocks.
 * Each tier differs in production speed, cycle ticks, and farm ball damage chances.
 */
public enum FarmTier {

    IRON(1, "iron", 200, 1.0f, new int[]{2, 3}),
    GOLD(2, "gold", 140, 1.5f, new int[]{1, 2}),
    DIAMOND(3, "diamond", 100, 2.0f, new int[]{1}),
    EMERALD(4, "emerald", 60, 3.0f, new int[]{0, 1}),
    NETHERITE(5, "netherite", 30, 5.0f, new int[]{0});

    /** Display tier number (1–5). */
    public final int tier;
    /** Registry name suffix (e.g. "iron" → "cobblefarm_iron"). */
    public final String name;
    /** Server ticks between each production cycle. */
    public final int ticksPerCycle;
    /** Multiplier applied to drop quantities. */
    public final float speedMultiplier;
    /** Array of possible damage amounts taken by the Farm Ball per cycle. */
    private final int[] damageChance;

    FarmTier(int tier, String name, int ticksPerCycle, float speedMultiplier, int[] damageChance) {
        this.tier = tier;
        this.name = name;
        this.ticksPerCycle = ticksPerCycle;
        this.speedMultiplier = speedMultiplier;
        this.damageChance = damageChance;
    }

    /**
     * Returns a random amount of damage to apply to the Farm Ball.
     */
    public int getRandomDamage(Random random) {
        return this.damageChance[random.nextInt(this.damageChance.length)];
    }
}
