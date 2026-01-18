package com.hyfactions.enchanter.ability;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * Represents a magical ability that can be triggered by enchanted items
 */
@Data
@Builder
public class MagicalAbility {

    // Basic properties
    private final String id;
    private final String displayName;
    private final List<String> description;
    private final AbilityType type;
    private final String tier;

    // Cooldown and cost
    private final long cooldown; // in seconds
    private final int manaCost;

    // Requirements
    @Singular
    private final Map<String, Integer> requiredEnchantments; // Enchantment ID -> minimum level

    @Singular
    private final List<String> requiredAbilities;

    // Activation
    private final AbilityTrigger trigger;
    private final String itemType;
    private final List<String> comboKeys;

    // Effects
    @Singular
    private final List<AbilityEffect> effects;

    // Audio/Visual
    private final String sound;
    private final String particle;

    // Additional properties
    private final Map<String, Object> properties;

    /**
     * Check if player has required enchantments
     * Implementation depends on checking player's equipped items
     */
    public boolean hasRequirements(Object player) {
        // Would check player's equipped enchantments
        return true;
    }

    /**
     * Get formatted cooldown display
     */
    public String getCooldownDisplay() {
        if (cooldown >= 3600) {
            return (cooldown / 3600) + "h";
        } else if (cooldown >= 60) {
            return (cooldown / 60) + "m";
        } else {
            return cooldown + "s";
        }
    }

    /**
     * Get formatted display name
     */
    public String getFormattedDisplayName() {
        return displayName.replace("&", "§");
    }

    /**
     * Get formatted description
     */
    public List<String> getFormattedDescription() {
        return description.stream()
                .map(line -> line.replace("&", "§").replace("{cooldown}", String.valueOf(cooldown)))
                .toList();
    }
}
