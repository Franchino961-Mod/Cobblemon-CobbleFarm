package com.cobblefarm.config;

import com.cobblefarm.CobbleFarm;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CobbleFarmConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("cobblefarm.json").toFile();

    private static CobbleFarmConfig instance = new CobbleFarmConfig();

    public boolean allowLegendaryCapture = false;
    public boolean allowMythicalCapture = false;
    public boolean allowUltraBeastCapture = false;
    public List<String> disallowedLabels = new ArrayList<>(List.of("legendary", "mythical", "ultra_beast"));
    public List<String> speciesBlacklist = new ArrayList<>();
    public List<String> speciesWhitelist = new ArrayList<>();

    public enum DisallowReason {
        ALLOWED,
        LEGENDARY_OR_MYTHICAL,
        BLACKLISTED,
        NOT_WHITELISTED,
        DISALLOWED_LABEL
    }

    public static CobbleFarmConfig getInstance() {
        return instance;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                CobbleFarmConfig loaded = GSON.fromJson(reader, CobbleFarmConfig.class);
                if (loaded != null) {
                    instance = loaded;
                    instance.sanitize();
                }
            } catch (Exception e) {
                CobbleFarm.LOGGER.error("Failed to load CobbleFarm config, using defaults", e);
                instance = new CobbleFarmConfig();
            }
        } else {
            instance = new CobbleFarmConfig();
            save();
        }
    }

    public static void save() {
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(instance, writer);
            }
        } catch (Exception e) {
            CobbleFarm.LOGGER.error("Failed to save CobbleFarm config", e);
        }
    }

    private void sanitize() {
        if (disallowedLabels == null) {
            disallowedLabels = new ArrayList<>(List.of("legendary", "mythical", "ultra_beast"));
        }
        if (speciesBlacklist == null) {
            speciesBlacklist = new ArrayList<>();
        }
        if (speciesWhitelist == null) {
            speciesWhitelist = new ArrayList<>();
        }
    }

    /**
     * Determines why a Pokémon is disallowed, or returns ALLOWED if permitted.
     */
    public static DisallowReason getDisallowReason(Pokemon pokemon) {
        if (pokemon == null) return DisallowReason.BLACKLISTED;

        String speciesId = pokemon.getSpecies().getResourceIdentifier().toString().toLowerCase(Locale.ROOT);
        String speciesPath = pokemon.getSpecies().getResourceIdentifier().getPath().toLowerCase(Locale.ROOT);

        // Blacklist check
        for (String blacklisted : instance.speciesBlacklist) {
            if (blacklisted == null || blacklisted.trim().isEmpty()) continue;
            String b = blacklisted.trim().toLowerCase(Locale.ROOT);
            if (b.equals(speciesId) || b.equals(speciesPath)) {
                return DisallowReason.BLACKLISTED;
            }
        }

        // Whitelist check
        if (instance.speciesWhitelist != null && !instance.speciesWhitelist.isEmpty()) {
            boolean whitelisted = false;
            for (String wEntry : instance.speciesWhitelist) {
                if (wEntry == null || wEntry.trim().isEmpty()) continue;
                String w = wEntry.trim().toLowerCase(Locale.ROOT);
                if (w.equals(speciesId) || w.equals(speciesPath)) {
                    whitelisted = true;
                    break;
                }
            }
            if (!whitelisted) {
                return DisallowReason.NOT_WHITELISTED;
            }
        }

        // Legendary check
        if (!instance.allowLegendaryCapture && pokemon.isLegendary()) {
            return DisallowReason.LEGENDARY_OR_MYTHICAL;
        }

        // Mythical check
        if (!instance.allowMythicalCapture && pokemon.isMythical()) {
            return DisallowReason.LEGENDARY_OR_MYTHICAL;
        }

        // Ultra Beast check
        if (!instance.allowUltraBeastCapture && pokemon.isUltraBeast()) {
            return DisallowReason.LEGENDARY_OR_MYTHICAL;
        }

        // Custom disallowed labels
        if (instance.disallowedLabels != null) {
            for (String label : instance.disallowedLabels) {
                if (label == null || label.trim().isEmpty()) continue;
                String normalized = label.trim().toLowerCase(Locale.ROOT);

                if (normalized.equals("legendary") && instance.allowLegendaryCapture) continue;
                if (normalized.equals("mythical") && instance.allowMythicalCapture) continue;
                if (normalized.equals("ultra_beast") && instance.allowUltraBeastCapture) continue;

                if (pokemon.hasLabels(normalized)) {
                    return DisallowReason.DISALLOWED_LABEL;
                }
                if (pokemon.getSpecies().getLabels() != null && pokemon.getSpecies().getLabels().contains(normalized)) {
                    return DisallowReason.DISALLOWED_LABEL;
                }
                if (pokemon.getForm() != null && pokemon.getForm().getLabels() != null && pokemon.getForm().getLabels().contains(normalized)) {
                    return DisallowReason.DISALLOWED_LABEL;
                }
            }
        }

        return DisallowReason.ALLOWED;
    }

    /**
     * Checks if a live Pokémon entity can be captured with a Farm Ball.
     */
    public static boolean isPokemonAllowed(Pokemon pokemon) {
        return getDisallowReason(pokemon) == DisallowReason.ALLOWED;
    }

    /**
     * Checks if a Farm Ball item stack containing a Pokémon is allowed according to the current config.
     */
    public static boolean isPokemonAllowed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        String speciesId = com.cobblefarm.item.FarmBallItem.getSpecies(stack);
        if (speciesId == null || speciesId.isEmpty() || speciesId.equals("unknown")) return false;
        return isSpeciesAllowed(speciesId);
    }

    /**
     * Checks if a species identifier (e.g. "cobblemon:rayquaza" or "rayquaza") is allowed.
     */
    public static boolean isSpeciesAllowed(String speciesId) {
        if (speciesId == null || speciesId.trim().isEmpty()) return false;
        String lowerId = speciesId.trim().toLowerCase(Locale.ROOT);

        for (String blacklisted : instance.speciesBlacklist) {
            if (blacklisted == null || blacklisted.trim().isEmpty()) continue;
            String b = blacklisted.trim().toLowerCase(Locale.ROOT);
            if (b.equals(lowerId) || lowerId.endsWith(":" + b)) {
                return false;
            }
        }

        if (instance.speciesWhitelist != null && !instance.speciesWhitelist.isEmpty()) {
            boolean whitelisted = false;
            for (String wEntry : instance.speciesWhitelist) {
                if (wEntry == null || wEntry.trim().isEmpty()) continue;
                String w = wEntry.trim().toLowerCase(Locale.ROOT);
                if (w.equals(lowerId) || lowerId.endsWith(":" + w)) {
                    whitelisted = true;
                    break;
                }
            }
            if (!whitelisted) {
                return false;
            }
        }

        try {
            Identifier id = Identifier.of(speciesId);
            Species species = PokemonSpecies.getByIdentifier(id);
            if (species != null) {
                Set<String> labels = species.getLabels();
                if (labels != null) {
                    if (!instance.allowLegendaryCapture && labels.contains("legendary")) {
                        return false;
                    }
                    if (!instance.allowMythicalCapture && labels.contains("mythical")) {
                        return false;
                    }
                    if (!instance.allowUltraBeastCapture && labels.contains("ultra_beast")) {
                        return false;
                    }
                    if (instance.disallowedLabels != null) {
                        for (String label : instance.disallowedLabels) {
                            if (label == null || label.trim().isEmpty()) continue;
                            String normalized = label.trim().toLowerCase(Locale.ROOT);
                            if (normalized.equals("legendary") && instance.allowLegendaryCapture) continue;
                            if (normalized.equals("mythical") && instance.allowMythicalCapture) continue;
                            if (normalized.equals("ultra_beast") && instance.allowUltraBeastCapture) continue;
                            if (labels.contains(normalized)) {
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        return true;
    }
}
