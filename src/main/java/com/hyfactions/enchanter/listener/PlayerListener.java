package com.hyfactions.enchanter.listener;

import com.hyfactions.enchanter.HyFactionsEnchanter;
import com.hyfactions.enchanter.util.PlayerUtil;

// Hytale Core API imports
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

import java.util.UUID;

/**
 * Handles player-related events
 */
public class PlayerListener {

    /**
     * Register all player events with Hytale EventRegistry
     */
    public static void register(EventRegistry eventRegistry) {
        // Register player ready event (replaces PlayerConnectEvent)
        eventRegistry.registerGlobal(PlayerReadyEvent.class, PlayerListener::handlePlayerReady);

        // TODO: Register additional events when Hytale API provides them:
        // - PlayerDisconnectEvent (for player quit)
        // - PlayerInteractEvent (for ability activation)
        // - PlayerToggleSneakEvent (for sneak-based abilities)
        // - PlayerMoveEvent (for movement abilities)
    }

    /**
     * Handle player ready - load player data
     */
    public static void handlePlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = PlayerUtil.getPlayerUUID(player);
        HyFactionsEnchanter plugin = HyFactionsEnchanter.getInstance();

        plugin.getLogger().info("Player " + PlayerUtil.getName(player) + " ready - loading enchantment data");

        // Load player's enchantment data from database asynchronously
        plugin.getTaskRegistry().async(() -> {
            // Load enchantments
            plugin.getDatabaseManager().getPlayerEnchantments(playerUuid);

            // Load abilities
            plugin.getDatabaseManager().getPlayerAbilities(playerUuid);

            plugin.getLogger().info("Loaded enchantment data for " + PlayerUtil.getName(player));
        });

        // Send welcome message
        plugin.getTaskRegistry().delayed(() -> {
            if (PlayerUtil.isOnline(player)) {
                player.sendMessage(Message.raw(""));
                player.sendMessage(Message.raw("§6§lHyFactions Enchanter"));
                player.sendMessage(Message.raw("§7Type §e/enchant §7for enchantment commands"));
                player.sendMessage(Message.raw("§7Type §e/abilities §7to view your magical abilities"));
                player.sendMessage(Message.raw(""));
            }
        }, 20); // 1 second delay (20 ticks)
    }

    /**
     * Handle player disconnect - save player data
     * TODO: Implement when Hytale provides PlayerDisconnectEvent
     */
    public static void handlePlayerDisconnect(Object event) {
        // This will be implemented when Hytale API provides disconnect events
        // Pseudocode for future implementation:

        // PlayerDisconnectEvent e = (PlayerDisconnectEvent) event;
        // Player player = e.getPlayer();
        // UUID playerUuid = PlayerUtil.getPlayerUUID(player);
        // HyFactionsEnchanter plugin = HyFactionsEnchanter.getInstance();
        //
        // plugin.getLogger().info("Player " + PlayerUtil.getName(player) + " disconnected - saving data");
        //
        // // Clear cooldowns from memory
        // plugin.getCooldownManager().clearPlayerCooldowns(playerUuid);
        //
        // // Save any pending data
        // plugin.getTaskRegistry().async(() -> {
        //     plugin.getLogger().info("Cleanup complete for " + PlayerUtil.getName(player));
        // });
    }

    /**
     * Handle player interact - for ability activation
     * TODO: Implement when Hytale provides PlayerInteractEvent
     */
    public static void handlePlayerInteract(Object event) {
        // This will be implemented when Hytale API provides interaction events
        // Check for ability activation (right-click, etc.)
        // Example:
        // Player player = event.getPlayer();
        // HyFactionsEnchanter plugin = HyFactionsEnchanter.getInstance();
        // if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
        //     plugin.getAbilityManager().tryActivateAbility(player, "right_click");
        // }
    }

    /**
     * Handle player toggle sneak - for sneak-based abilities
     * TODO: Implement when Hytale provides PlayerToggleSneakEvent
     */
    public static void handlePlayerToggleSneak(Object event) {
        // Track sneak for double-sneak abilities
        // Example:
        // Player player = event.getPlayer();
        // HyFactionsEnchanter plugin = HyFactionsEnchanter.getInstance();
        // if (event.isSneaking()) {
        //     plugin.getAbilityManager().trackSneak(player);
        // }
    }
}
