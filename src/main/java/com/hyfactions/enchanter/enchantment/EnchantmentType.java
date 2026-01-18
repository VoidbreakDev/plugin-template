package com.hyfactions.enchanter.enchantment;

import lombok.Getter;

/**
 * Types of items that can receive enchantments
 */
@Getter
public enum EnchantmentType {
    WEAPON("Weapon", "SWORD", "AXE", "BOW"),
    ARMOR("Armor", "HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"),
    TOOL("Tool", "PICKAXE", "SHOVEL", "AXE", "HOE"),
    ALL("All", "*");

    private final String displayName;
    private final String[] applicableItems;

    EnchantmentType(String displayName, String... applicableItems) {
        this.displayName = displayName;
        this.applicableItems = applicableItems;
    }

    /**
     * Check if an item type is applicable for this enchantment type
     */
    public boolean isApplicable(String itemType) {
        if (applicableItems.length == 1 && applicableItems[0].equals("*")) {
            return true;
        }

        for (String applicable : applicableItems) {
            if (itemType.toUpperCase().contains(applicable)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get type from string name
     */
    public static EnchantmentType fromString(String name) {
        for (EnchantmentType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return ALL;
    }
}
