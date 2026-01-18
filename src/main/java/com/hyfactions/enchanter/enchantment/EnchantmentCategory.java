package com.hyfactions.enchanter.enchantment;

import lombok.Getter;

/**
 * Categories for different enchantment types
 */
@Getter
public enum EnchantmentCategory {
    COMBAT("Combat", "Offensive and defensive combat enhancements"),
    UTILITY("Utility", "Tools and quality of life improvements"),
    FACTION("Faction", "Faction-specific bonuses and abilities");

    private final String displayName;
    private final String description;

    EnchantmentCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Get category from string name
     */
    public static EnchantmentCategory fromString(String name) {
        for (EnchantmentCategory category : values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return COMBAT;
    }
}
