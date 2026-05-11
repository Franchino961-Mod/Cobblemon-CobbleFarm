package com.cobblefarm.block;

/**
 * Defines the five tiers of CobbleFarm blocks.
 * Each tier differs in production speed and cycle ticks.
 */
public enum FarmTier {

    IRON(1, "iron", 200, 1.0f),
    GOLD(2, "gold", 140, 1.5f),
    DIAMOND(3, "diamond", 100, 2.0f),
    EMERALD(4, "emerald", 60, 3.0f),
    NETHERITE(5, "netherite", 30, 5.0f);

    /** Display tier number (1–5). */
    public final int tier;
    /** Registry name suffix (e.g. "iron" → "cobblefarm_iron"). */
    public final String name;
    /** Server ticks between each production cycle. */
    public final int ticksPerCycle;
    /** Multiplier applied to drop quantities. */
    public final float speedMultiplier;

    FarmTier(int tier, String name, int ticksPerCycle, float speedMultiplier) {
        this.tier = tier;
        this.name = name;
        this.ticksPerCycle = ticksPerCycle;
        this.speedMultiplier = speedMultiplier;
    }
}
