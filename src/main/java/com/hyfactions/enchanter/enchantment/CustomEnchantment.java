package com.hyfactions.enchanter.enchantment;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * Represents a custom enchantment with all its properties
 * This is the core data structure for enchantments
 */
@Data
@Builder
public class CustomEnchantment {

    // Basic properties
    private final String id;
    private final String displayName;
    private final List<String> description;
    private final EnchantmentTier tier;
    private final EnchantmentCategory category;
    private final EnchantmentType type;

    // Level configuration
    private final int maxLevel;
    private final Map<Integer, EnchantmentLevel> levels;

    // Applicability
    @Singular
    private final List<String> applicableItems;

    // Compatibility
    @Singular
    private final List<String> conflicts;

    @Singular
    private final List<String> synergies;

    // Requirements
    private final boolean requiresFaction;
    private final int requiredFactionPower;

    // Visual effects
    private final String particle;
    private final String sound;

    // Custom properties for specific enchantments
    private final Map<String, Object> customProperties;

    /**
     * Check if this enchantment can be applied to a specific item type
     */
    public boolean canApplyTo(String itemType) {
        if (applicableItems.isEmpty()) {
            return type.isApplicable(itemType);
        }

        for (String applicable : applicableItems) {
            if (itemType.toUpperCase().contains(applicable.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this enchantment conflicts with another
     */
    public boolean conflictsWith(String enchantmentId) {
        return conflicts.contains(enchantmentId);
    }

    /**
     * Check if this enchantment has synergy with another
     */
    public boolean hasSynergyWith(String enchantmentId) {
        return synergies.contains(enchantmentId);
    }

    /**
     * Get the properties for a specific level
     */
    public EnchantmentLevel getLevel(int level) {
        if (level < 1 || level > maxLevel) {
            return null;
        }
        return levels.get(level);
    }

    /**
     * Get the cost for a specific level
     */
    public double getCost(int level) {
        EnchantmentLevel enchLevel = getLevel(level);
        if (enchLevel == null) {
            return 0;
        }
        return enchLevel.getCost();
    }

    /**
     * Get a custom property value
     */
    public Object getProperty(String key) {
        if (customProperties == null) {
            return null;
        }
        return customProperties.get(key);
    }

    /**
     * Get a custom property as a specific type
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, Class<T> type) {
        Object value = getProperty(key);
        if (value == null) {
            return null;
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Get formatted display name with color codes
     */
    public String getFormattedDisplayName() {
        return displayName.replace("&", "§");
    }

    /**
     * Get formatted description with color codes
     */
    public List<String> getFormattedDescription() {
        return description.stream()
                .map(line -> line.replace("&", "§"))
                .toList();
    }

    /**
     * Represents a specific level of an enchantment
     */
    @Data
    @Builder
    public static class EnchantmentLevel {
        private final int level;
        private final double cost;
        private final Map<String, Object> properties;

        /**
         * Get a property value for this level
         */
        public Object getProperty(String key) {
            if (properties == null) {
                return null;
            }
            return properties.get(key);
        }

        /**
         * Get a property as a specific type
         */
        @SuppressWarnings("unchecked")
        public <T> T getProperty(String key, Class<T> type, T defaultValue) {
            Object value = getProperty(key);
            if (value == null) {
                return defaultValue;
            }
            try {
                // Handle numeric conversions
                if (type == Double.class && value instanceof Number) {
                    return (T) Double.valueOf(((Number) value).doubleValue());
                }
                if (type == Integer.class && value instanceof Number) {
                    return (T) Integer.valueOf(((Number) value).intValue());
                }
                return (T) value;
            } catch (ClassCastException e) {
                return defaultValue;
            }
        }

        /**
         * Get integer property
         */
        public int getIntProperty(String key, int defaultValue) {
            return getProperty(key, Integer.class, defaultValue);
        }

        /**
         * Get double property
         */
        public double getDoubleProperty(String key, double defaultValue) {
            return getProperty(key, Double.class, defaultValue);
        }

        /**
         * Get string property
         */
        public String getStringProperty(String key, String defaultValue) {
            return getProperty(key, String.class, defaultValue);
        }

        /**
         * Get boolean property
         */
        public boolean getBooleanProperty(String key, boolean defaultValue) {
            return getProperty(key, Boolean.class, defaultValue);
        }
    }
}
