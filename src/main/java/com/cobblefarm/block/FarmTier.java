package com.cobblefarm.block;

/**
 * Defines the five tiers of Pokémon Farm blocks.
 * Each tier differs in production speed, cycle ticks, and buffer capacity.
 */
public enum FarmTier {

    IRON(1, "iron", 200, 1.0f, 64),
    GOLD(2, "gold", 140, 1.5f, 128),
    DIAMOND(3, "diamond", 100, 2.0f, 256),
    EMERALD(4, "emerald", 60, 3.0f, 512),
    NETHERITE(5, "netherite", 30, 5.0f, 1024);

    /** Display tier number (1–5). */
    public final int tier;
    /** Registry name suffix (e.g. "iron" → "pokemon_farm_iron"). */
    public final String name;
    /** Server ticks between each production cycle. */
    public final int ticksPerCycle;
    /** Multiplier applied to drop quantities. */
    public final float speedMultiplier;
    /** Maximum number of item stacks the internal buffer can hold (in items, not stacks). */
    public final int bufferCapacity;

    FarmTier(int tier, String name, int ticksPerCycle, float speedMultiplier, int bufferCapacity) {
        this.tier = tier;
        this.name = name;
        this.ticksPerCycle = ticksPerCycle;
        this.speedMultiplier = speedMultiplier;
        this.bufferCapacity = bufferCapacity;
    }
}
