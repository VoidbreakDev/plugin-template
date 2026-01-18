package com.hyfactions.enchanter.enchantment;

import lombok.Getter;

/**
 * Represents the tier/rarity of an enchantment
 */
@Getter
public enum EnchantmentTier {
    COMMON("&7Common", 1.0, 0.95),
    RARE("&9Rare", 2.5, 0.80),
    EPIC("&5Epic", 5.0, 0.60),
    LEGENDARY("&6Legendary", 10.0, 0.40);

    private final String displayName;
    private final double costMultiplier;
    private final double successRate;

    EnchantmentTier(String displayName, double costMultiplier, double successRate) {
        this.displayName = displayName;
        this.costMultiplier = costMultiplier;
        this.successRate = successRate;
    }

    /**
     * Get tier from string name
     */
    public static EnchantmentTier fromString(String name) {
        for (EnchantmentTier tier : values()) {
            if (tier.name().equalsIgnoreCase(name)) {
                return tier;
            }
        }
        return COMMON;
    }
}
