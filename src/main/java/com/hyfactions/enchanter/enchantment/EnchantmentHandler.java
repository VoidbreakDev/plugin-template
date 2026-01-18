package com.hyfactions.enchanter.enchantment;

/**
 * Base interface for enchantment handlers
 * Each enchantment implements this to define its behavior
 */
public interface EnchantmentHandler {

    /**
     * Get the enchantment definition
     */
    CustomEnchantment getEnchantment();

    /**
     * Called when the enchantment is applied to an item
     * Can be used for initialization or validation
     *
     * @param context The application context
     * @return true if successful, false otherwise
     */
    default boolean onApply(EnchantmentContext context) {
        return true;
    }

    /**
     * Called when the enchantment is removed from an item
     *
     * @param context The removal context
     */
    default void onRemove(EnchantmentContext context) {
        // Default: do nothing
    }

    /**
     * Called periodically while the enchantment is active
     * Useful for passive effects
     *
     * @param context The tick context
     */
    default void onTick(EnchantmentContext context) {
        // Default: do nothing
    }

    /**
     * Get the priority for this enchantment
     * Higher priority enchantments are processed first
     * Default is 0
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Check if this enchantment should be processed in the current context
     * Can be used to add conditional behavior
     */
    default boolean shouldProcess(EnchantmentContext context) {
        return true;
    }
}
