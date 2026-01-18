package com.hyfactions.enchanter.enchantment;

import com.hyfactions.enchanter.HyFactionsEnchanter;
import com.hyfactions.enchanter.enchantment.impl.*;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages all custom enchantments
 * Handles registration, lookup, and enchantment operations
 */
public class EnchantmentManager {

    private final HyFactionsEnchanter plugin;

    @Getter
    private final Map<String, CustomEnchantment> enchantments;

    @Getter
    private final Map<String, EnchantmentHandler> handlers;

    // Cache for performance
    private final Map<EnchantmentTier, List<CustomEnchantment>> enchantmentsByTier;
    private final Map<EnchantmentCategory, List<CustomEnchantment>> enchantmentsByCategory;

    public EnchantmentManager(HyFactionsEnchanter plugin) {
        this.plugin = plugin;
        this.enchantments = new ConcurrentHashMap<>();
        this.handlers = new ConcurrentHashMap<>();
        this.enchantmentsByTier = new EnumMap<>(EnchantmentTier.class);
        this.enchantmentsByCategory = new EnumMap<>(EnchantmentCategory.class);
    }

    /**
     * Load all enchantments from configuration
     */
    public void loadEnchantments() {
        plugin.getLogger().info("Loading enchantments...");

        // Clear existing data
        enchantments.clear();
        handlers.clear();
        enchantmentsByTier.clear();
        enchantmentsByCategory.clear();

        // Load enchantments from config
        // ConfigurationSection enchantsConfig = plugin.getConfig().getConfigurationSection("enchantments");
        // This would load from enchantments.yml in a real implementation

        // For now, register hardcoded enchantments
        registerDefaultEnchantments();

        // Build caches
        buildCaches();

        plugin.getLogger().info("Loaded " + enchantments.size() + " enchantments.");
    }

    /**
     * Register default enchantments
     * In production, these would be loaded from enchantments.yml
     */
    private void registerDefaultEnchantments() {
        // Combat enchantments
        registerEnchantment(new LifestealEnchantment());
        registerEnchantment(new CriticalStrikeEnchantment());
        registerEnchantment(new SharpnessBoostEnchantment());
        registerEnchantment(new PoisonStrikeEnchantment());
        registerEnchantment(new FireAspectBoostEnchantment());

        // Armor enchantments
        registerEnchantment(new IronSkinEnchantment());
        registerEnchantment(new ThornsBoostEnchantment());
        registerEnchantment(new RegenerationAuraEnchantment());
        registerEnchantment(new FeatherFallingBoostEnchantment());

        // Utility enchantments
        registerEnchantment(new HasteEnchantment());
        registerEnchantment(new AutoSmeltEnchantment());
        registerEnchantment(new FortuneBoostEnchantment());
        registerEnchantment(new SilkTouchBoostEnchantment());
        registerEnchantment(new ExperienceBoostEnchantment());
        registerEnchantment(new SpeedEnchantment());

        // Faction enchantments
        registerEnchantment(new TerritoryGuardEnchantment());
        registerEnchantment(new RaidMasterEnchantment());
        registerEnchantment(new ResourceBlessingEnchantment());
        registerEnchantment(new AllyProtectionEnchantment());
    }

    /**
     * Register a custom enchantment
     */
    public void registerEnchantment(EnchantmentHandler handler) {
        CustomEnchantment enchantment = handler.getEnchantment();
        String id = enchantment.getId().toUpperCase();

        if (enchantments.containsKey(id)) {
            plugin.getLogger().warning("Enchantment " + id + " is already registered! Skipping...");
            return;
        }

        enchantments.put(id, enchantment);
        handlers.put(id, handler);

        plugin.getLogger().fine("Registered enchantment: " + id);
    }

    /**
     * Get an enchantment by ID
     */
    public CustomEnchantment getEnchantment(String id) {
        return enchantments.get(id.toUpperCase());
    }

    /**
     * Get an enchantment handler by ID
     */
    public EnchantmentHandler getHandler(String id) {
        return handlers.get(id.toUpperCase());
    }

    /**
     * Get all enchantments of a specific tier
     */
    public List<CustomEnchantment> getEnchantmentsByTier(EnchantmentTier tier) {
        return enchantmentsByTier.getOrDefault(tier, Collections.emptyList());
    }

    /**
     * Get all enchantments of a specific category
     */
    public List<CustomEnchantment> getEnchantmentsByCategory(EnchantmentCategory category) {
        return enchantmentsByCategory.getOrDefault(category, Collections.emptyList());
    }

    /**
     * Get all enchantments applicable to an item type
     */
    public List<CustomEnchantment> getApplicableEnchantments(String itemType) {
        return enchantments.values().stream()
                .filter(e -> e.canApplyTo(itemType))
                .collect(Collectors.toList());
    }

    /**
     * Check if two enchantments conflict
     */
    public boolean hasConflict(String enchantId1, String enchantId2) {
        CustomEnchantment ench1 = getEnchantment(enchantId1);
        CustomEnchantment ench2 = getEnchantment(enchantId2);

        if (ench1 == null || ench2 == null) {
            return false;
        }

        return ench1.conflictsWith(enchantId2) || ench2.conflictsWith(enchantId1);
    }

    /**
     * Check if two enchantments have synergy
     */
    public boolean hasSynergy(String enchantId1, String enchantId2) {
        CustomEnchantment ench1 = getEnchantment(enchantId1);
        CustomEnchantment ench2 = getEnchantment(enchantId2);

        if (ench1 == null || ench2 == null) {
            return false;
        }

        return ench1.hasSynergyWith(enchantId2) || ench2.hasSynergyWith(enchantId1);
    }

    /**
     * Get the total count of registered enchantments
     */
    public int getEnchantmentCount() {
        return enchantments.size();
    }

    /**
     * Build internal caches for fast lookup
     */
    private void buildCaches() {
        // Cache by tier
        for (EnchantmentTier tier : EnchantmentTier.values()) {
            List<CustomEnchantment> tierList = enchantments.values().stream()
                    .filter(e -> e.getTier() == tier)
                    .collect(Collectors.toList());
            enchantmentsByTier.put(tier, tierList);
        }

        // Cache by category
        for (EnchantmentCategory category : EnchantmentCategory.values()) {
            List<CustomEnchantment> categoryList = enchantments.values().stream()
                    .filter(e -> e.getCategory() == category)
                    .collect(Collectors.toList());
            enchantmentsByCategory.put(category, categoryList);
        }
    }

    /**
     * Reload enchantments from configuration
     */
    public void reloadEnchantments() {
        plugin.getLogger().info("Reloading enchantments...");
        loadEnchantments();
    }

    /**
     * Shutdown the enchantment manager
     */
    public void shutdown() {
        plugin.getLogger().info("Shutting down enchantment manager...");

        // Clear all data
        enchantments.clear();
        handlers.clear();
        enchantmentsByTier.clear();
        enchantmentsByCategory.clear();
    }

    /**
     * Get all registered enchantment IDs
     */
    public Set<String> getEnchantmentIds() {
        return new HashSet<>(enchantments.keySet());
    }

    /**
     * Check if an enchantment exists
     */
    public boolean hasEnchantment(String id) {
        return enchantments.containsKey(id.toUpperCase());
    }
}
