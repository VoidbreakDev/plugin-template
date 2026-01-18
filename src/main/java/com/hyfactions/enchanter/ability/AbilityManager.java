package com.hyfactions.enchanter.ability;

import com.hyfactions.enchanter.HyFactionsEnchanter;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all magical abilities
 * Handles cooldowns, activation, and execution
 */
public class AbilityManager {

    private final HyFactionsEnchanter plugin;

    @Getter
    private final Map<String, MagicalAbility> abilities;

    // Cooldown tracking: PlayerUUID -> AbilityID -> ExpiryTime
    private final Map<UUID, Map<String, Long>> cooldowns;

    // Active abilities tracking
    private final Map<UUID, Set<String>> activeAbilities;

    public AbilityManager(HyFactionsEnchanter plugin) {
        this.plugin = plugin;
        this.abilities = new ConcurrentHashMap<>();
        this.cooldowns = new ConcurrentHashMap<>();
        this.activeAbilities = new ConcurrentHashMap<>();
    }

    /**
     * Load all abilities from configuration
     */
    public void loadAbilities() {
        plugin.getLogger().info("Loading abilities...");

        abilities.clear();
        // Would load from abilities.yml

        plugin.getLogger().info("Loaded " + abilities.size() + " abilities.");
    }

    /**
     * Register an ability
     */
    public void registerAbility(MagicalAbility ability) {
        abilities.put(ability.getId().toUpperCase(), ability);
    }

    /**
     * Get an ability by ID
     */
    public MagicalAbility getAbility(String id) {
        return abilities.get(id.toUpperCase());
    }

    /**
     * Check if a player can use an ability (cooldown check)
     */
    public boolean canUseAbility(UUID playerUuid, String abilityId) {
        if (!cooldowns.containsKey(playerUuid)) {
            return true;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(playerUuid);
        if (!playerCooldowns.containsKey(abilityId)) {
            return true;
        }

        long expiryTime = playerCooldowns.get(abilityId);
        return System.currentTimeMillis() >= expiryTime;
    }

    /**
     * Get remaining cooldown time in seconds
     */
    public long getRemainingCooldown(UUID playerUuid, String abilityId) {
        if (!cooldowns.containsKey(playerUuid)) {
            return 0;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(playerUuid);
        if (!playerCooldowns.containsKey(abilityId)) {
            return 0;
        }

        long expiryTime = playerCooldowns.get(abilityId);
        long remaining = (expiryTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * Set cooldown for a player's ability
     */
    public void setCooldown(UUID playerUuid, String abilityId, long cooldownSeconds) {
        cooldowns.computeIfAbsent(playerUuid, k -> new ConcurrentHashMap<>())
                .put(abilityId, System.currentTimeMillis() + (cooldownSeconds * 1000));
    }

    /**
     * Clear a player's cooldown for an ability
     */
    public void clearCooldown(UUID playerUuid, String abilityId) {
        if (cooldowns.containsKey(playerUuid)) {
            cooldowns.get(playerUuid).remove(abilityId);
        }
    }

    /**
     * Clear all cooldowns for a player
     */
    public void clearAllCooldowns(UUID playerUuid) {
        cooldowns.remove(playerUuid);
    }

    /**
     * Activate an ability for a player
     */
    public boolean activateAbility(UUID playerUuid, String abilityId, Object player) {
        MagicalAbility ability = getAbility(abilityId);
        if (ability == null) {
            return false;
        }

        // Check cooldown
        if (!canUseAbility(playerUuid, abilityId)) {
            return false;
        }

        // Check requirements
        if (!ability.hasRequirements(player)) {
            return false;
        }

        // Execute ability effects
        executeAbility(ability, player);

        // Set cooldown
        if (ability.getCooldown() > 0) {
            setCooldown(playerUuid, abilityId, ability.getCooldown());
        }

        // Track active ability
        activeAbilities.computeIfAbsent(playerUuid, k -> ConcurrentHashMap.newKeySet())
                .add(abilityId);

        return true;
    }

    /**
     * Execute ability effects
     */
    private void executeAbility(MagicalAbility ability, Object player) {
        // Would execute each effect based on its type
        for (AbilityEffect effect : ability.getEffects()) {
            executeEffect(effect, player);
        }

        // Play sound
        if (ability.getSound() != null) {
            // Play sound effect
        }
    }

    /**
     * Execute a single ability effect
     */
    private void executeEffect(AbilityEffect effect, Object player) {
        switch (effect.getType()) {
            case LIGHTNING:
                // Execute lightning strike
                break;
            case HEAL:
                // Execute healing
                break;
            case TELEPORT:
                // Execute teleport
                break;
            case POTION:
                // Apply potion effect
                break;
            // ... other effect types
        }
    }

    /**
     * Get total ability count
     */
    public int getAbilityCount() {
        return abilities.size();
    }

    /**
     * Get all ability IDs
     */
    public Set<String> getAbilityIds() {
        return new HashSet<>(abilities.keySet());
    }

    /**
     * Get abilities by type
     */
    public List<MagicalAbility> getAbilitiesByType(AbilityType type) {
        return abilities.values().stream()
                .filter(a -> a.getType() == type)
                .toList();
    }

    /**
     * Reload abilities from configuration
     */
    public void reloadAbilities() {
        plugin.getLogger().info("Reloading abilities...");
        loadAbilities();
    }

    /**
     * Shutdown the ability manager
     */
    public void shutdown() {
        plugin.getLogger().info("Shutting down ability manager...");
        cooldowns.clear();
        activeAbilities.clear();
        abilities.clear();
    }
}
