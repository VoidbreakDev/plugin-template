package com.hyfactions.enchanter.util;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.entity.LivingEntity;
import com.hypixel.hytale.server.core.item.ItemStack;
import com.hypixel.hytale.server.core.world.World;
import com.hypixel.hytale.math.Position;
import com.hypixel.hytale.component.health.HealthComponent;

import java.util.UUID;

/**
 * Utility class for working with Hytale players
 * Handles thread-safe component access
 */
public class PlayerUtil {

    /**
     * Heal a player
     * Must be called from world thread or wrapped in world.execute()
     *
     * @param player The player to heal
     * @param amount Amount of health to restore
     */
    public static void healPlayer(Player player, double amount) {
        World world = player.getWorld();

        // Execute on world thread for component access
        world.execute(() -> {
            HealthComponent health = player.getComponent(HealthComponent.class);
            if (health != null) {
                double newHealth = Math.min(
                        health.getMaxHealth(),
                        health.getHealth() + amount
                );
                health.setHealth(newHealth);
            }
        });
    }

    /**
     * Damage a player
     * Must be called from world thread or wrapped in world.execute()
     *
     * @param player The player to damage
     * @param amount Amount of damage to deal
     */
    public static void damagePlayer(Player player, double amount) {
        World world = player.getWorld();

        world.execute(() -> {
            HealthComponent health = player.getComponent(HealthComponent.class);
            if (health != null) {
                double newHealth = Math.max(0, health.getHealth() - amount);
                health.setHealth(newHealth);
            }
        });
    }

    /**
     * Get player's current health
     * Must be called from world thread
     *
     * @param player The player
     * @return Current health value
     */
    public static double getHealth(Player player) {
        HealthComponent health = player.getComponent(HealthComponent.class);
        return health != null ? health.getHealth() : 0.0;
    }

    /**
     * Get player's maximum health
     * Must be called from world thread
     *
     * @param player The player
     * @return Maximum health value
     */
    public static double getMaxHealth(Player player) {
        HealthComponent health = player.getComponent(HealthComponent.class);
        return health != null ? health.getMaxHealth() : 20.0;
    }

    /**
     * Get player's held item
     *
     * @param player The player
     * @return The held ItemStack, or null
     */
    public static ItemStack getHeldItem(Player player) {
        return ItemUtil.getHeldItem(player);
    }

    /**
     * Get player UUID
     *
     * @param player The player
     * @return Player's UUID
     */
    public static UUID getPlayerUUID(Player player) {
        return player.getPlayerRef().getUuid();
    }

    /**
     * Teleport player to a position
     * Must be called from world thread or wrapped in world.execute()
     *
     * @param player   The player to teleport
     * @param position The target position
     */
    public static void teleportPlayer(Player player, Position position) {
        player.getWorld().execute(() -> {
            player.setPosition(position);
        });
    }

    /**
     * Get player's current position
     *
     * @param player The player
     * @return Player's position
     */
    public static Position getPosition(Player player) {
        return player.getPosition();
    }

    /**
     * Get player's world
     *
     * @param player The player
     * @return The world the player is in
     */
    public static World getWorld(Player player) {
        return player.getWorld();
    }

    /**
     * Send a message to a player
     *
     * @param player  The player
     * @param message The message to send
     */
    public static void sendMessage(Player player, String message) {
        // Colorize the message
        String colored = message.replace("&", "§");
        player.sendMessage(colored);
    }

    /**
     * Apply a potion effect to a player
     * TODO: Implement when Hytale effect system is available
     *
     * @param player    The player
     * @param effectType The effect type
     * @param duration  Duration in seconds
     * @param amplifier Effect amplifier (level - 1)
     */
    public static void applyEffect(Player player, String effectType, int duration, int amplifier) {
        player.getWorld().execute(() -> {
            // Note: This will need to use Hytale's actual effect component
            // Pseudocode for future implementation:
            // EffectComponent effects = player.getComponent(EffectComponent.class);
            // if (effects != null) {
            //     effects.addEffect(effectType, duration * 20, amplifier);
            // }
        });
    }

    /**
     * Remove a potion effect from a player
     * TODO: Implement when Hytale effect system is available
     *
     * @param player    The player
     * @param effectType The effect type to remove
     */
    public static void removeEffect(Player player, String effectType) {
        player.getWorld().execute(() -> {
            // EffectComponent effects = player.getComponent(EffectComponent.class);
            // if (effects != null) {
            //     effects.removeEffect(effectType);
            // }
        });
    }

    /**
     * Check if player has permission
     *
     * @param player     The player
     * @param permission The permission node
     * @return true if player has permission
     */
    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }

    /**
     * Get player's name
     *
     * @param player The player
     * @return Player's name
     */
    public static String getName(Player player) {
        return player.getName();
    }

    /**
     * Check if player is online
     *
     * @param player The player
     * @return true if online
     */
    public static boolean isOnline(Player player) {
        // TODO: Implement based on Hytale's player state system
        return true; // Placeholder
    }

    /**
     * Execute code safely on the player's world thread
     *
     * @param player   The player
     * @param runnable The code to execute
     */
    public static void executeOnWorldThread(Player player, Runnable runnable) {
        player.getWorld().execute(runnable);
    }
}
