package com.hyfactions.enchanter.enchantment;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.item.ItemStack;
import com.hypixel.hytale.server.core.world.World;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Context object passed to enchantment handlers
 * Contains all relevant information about the enchantment event
 */
@Data
@Builder
public class EnchantmentContext {

    // Core objects with Hytale types
    private Player player;
    private ItemStack item;
    private Object event; // Keep as Object since event types vary
    private World world;

    // Enchantment details
    private String enchantmentId;
    private int level;

    // Event-specific data
    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

    // Faction context (if applicable)
    private Object faction; // Depends on faction plugin
    private boolean inFactionTerritory;
    private boolean inRaid;

    /**
     * Add custom data to the context
     */
    public void setData(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Get custom data from the context
     */
    public Object getData(String key) {
        return data.get(key);
    }

    /**
     * Get data with type casting
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> type) {
        Object value = data.get(key);
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
     * Check if data exists
     */
    public boolean hasData(String key) {
        return data.containsKey(key);
    }

    /**
     * Get event as a specific type
     */
    @SuppressWarnings("unchecked")
    public <T> T getEvent(Class<T> type) {
        try {
            return (T) event;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Get faction as a specific type
     */
    @SuppressWarnings("unchecked")
    public <T> T getFaction(Class<T> type) {
        try {
            return (T) faction;
        } catch (ClassCastException e) {
            return null;
        }
    }
}
