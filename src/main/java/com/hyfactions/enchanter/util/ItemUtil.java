package com.hyfactions.enchanter.util;

import com.hypixel.hytale.server.core.item.ItemStack;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.component.ComponentStore;
import com.hypixel.hytale.component.Component;

import java.util.*;

/**
 * Utility class for working with Hytale items and enchantments
 */
public class ItemUtil {

    // Custom component key for storing enchantments
    public static final String ENCHANTMENT_COMPONENT = "hyfactions:enchantments";

    /**
     * Get enchantments from an item
     * Reads from the item's component store
     *
     * @param item The item to check
     * @return Map of enchantment ID to level
     */
    public static Map<String, Integer> getItemEnchantments(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return Collections.emptyMap();
        }

        // Access item components
        ComponentStore components = item.getComponents();

        // Check if has enchantment component
        if (!components.has(ENCHANTMENT_COMPONENT)) {
            return Collections.emptyMap();
        }

        // Retrieve enchantment data
        Component enchantComponent = components.get(ENCHANTMENT_COMPONENT);
        Map<String, Integer> enchantments = new HashMap<>();

        // Parse enchantment data from component
        // Format: "ENCHANT_ID:LEVEL,ENCHANT_ID:LEVEL,..."
        String data = enchantComponent.asString();
        if (data == null || data.isEmpty()) {
            return Collections.emptyMap();
        }

        for (String entry : data.split(",")) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                try {
                    enchantments.put(parts[0], Integer.parseInt(parts[1]));
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }

        return enchantments;
    }

    /**
     * Set enchantments on an item
     * Writes to the item's component store
     *
     * @param item         The item to modify
     * @param enchantments Map of enchantment ID to level
     */
    public static void setItemEnchantments(ItemStack item, Map<String, Integer> enchantments) {
        if (item == null || item.isEmpty()) return;

        ComponentStore components = item.getComponents();

        // Build enchantment data string
        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            if (data.length() > 0) data.append(",");
            data.append(entry.getKey()).append(":").append(entry.getValue());
        }

        // Store in component
        components.set(ENCHANTMENT_COMPONENT, Component.string(data.toString()));

        // Update item lore to show enchantments
        updateItemLore(item, enchantments);
    }

    /**
     * Add an enchantment to an item
     *
     * @param item         The item to modify
     * @param enchantmentId The enchantment to add
     * @param level        The enchantment level
     */
    public static void addEnchantment(ItemStack item, String enchantmentId, int level) {
        Map<String, Integer> enchantments = new HashMap<>(getItemEnchantments(item));
        enchantments.put(enchantmentId, level);
        setItemEnchantments(item, enchantments);
    }

    /**
     * Remove an enchantment from an item
     *
     * @param item         The item to modify
     * @param enchantmentId The enchantment to remove
     */
    public static void removeEnchantment(ItemStack item, String enchantmentId) {
        Map<String, Integer> enchantments = new HashMap<>(getItemEnchantments(item));
        enchantments.remove(enchantmentId);
        setItemEnchantments(item, enchantments);
    }

    /**
     * Check if an item has a specific enchantment
     *
     * @param item         The item to check
     * @param enchantmentId The enchantment to look for
     * @return true if the item has the enchantment
     */
    public static boolean hasEnchantment(ItemStack item, String enchantmentId) {
        return getItemEnchantments(item).containsKey(enchantmentId);
    }

    /**
     * Get the level of a specific enchantment on an item
     *
     * @param item         The item to check
     * @param enchantmentId The enchantment to check
     * @return The enchantment level, or 0 if not present
     */
    public static int getEnchantmentLevel(ItemStack item, String enchantmentId) {
        return getItemEnchantments(item).getOrDefault(enchantmentId, 0);
    }

    /**
     * Update item lore to display enchantments
     * TODO: Implement lore system when Hytale provides it
     */
    private static void updateItemLore(ItemStack item, Map<String, Integer> enchantments) {
        // Note: This will need to be implemented when Hytale's
        // item display/lore system is available

        // Pseudocode for future implementation:
        // List<String> lore = new ArrayList<>();
        // lore.add("");
        // lore.add("§6§lEnchantments:");
        // for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
        //     String displayName = getEnchantmentDisplayName(entry.getKey());
        //     String level = getRomanNumeral(entry.getValue());
        //     lore.add("§7• " + displayName + " " + level);
        // }
        // item.setLore(lore);
    }

    /**
     * Convert number to Roman numeral
     */
    public static String getRomanNumeral(int number) {
        if (number < 1 || number > 10) return String.valueOf(number);

        String[] numerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return numerals[number];
    }

    /**
     * Get a player's held item
     * Must be called from world thread
     *
     * @param player The player
     * @return The held item, or null if empty
     */
    public static ItemStack getHeldItem(Player player) {
        // Note: This assumes Hytale has similar inventory structure
        // Adjust based on actual API
        try {
            var inventory = player.getInventory();
            int slot = inventory.getSelectedHotbarSlot();
            return inventory.getHotbar().get(slot);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if an item is empty or null
     */
    public static boolean isNullOrEmpty(ItemStack item) {
        return item == null || item.isEmpty();
    }

    /**
     * Create a unique identifier for an item
     * Used for database storage
     */
    public static UUID getItemUUID(ItemStack item) {
        // TODO: Implement proper item UUID system
        // For now, generate random UUID
        // In production, this should be persistent
        return UUID.randomUUID();
    }
}
