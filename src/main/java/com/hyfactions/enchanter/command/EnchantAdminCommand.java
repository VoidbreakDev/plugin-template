package com.hyfactions.enchanter.command;

import com.hyfactions.enchanter.HyFactionsEnchanter;
import com.hyfactions.enchanter.enchantment.CustomEnchantment;
import com.hyfactions.enchanter.util.PlayerUtil;
import com.hyfactions.enchanter.util.ItemUtil;

// Hytale API imports
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.item.ItemStack;
import com.hypixel.hytale.server.core.message.Message;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Admin command handler for enchantment system
 * /enchantadmin <reload|give|remove|stats|clear> [args]
 */
public class EnchantAdminCommand extends AbstractCommand {

    private final HyFactionsEnchanter plugin;

    public EnchantAdminCommand(HyFactionsEnchanter plugin) {
        super("enchantadmin", "Admin command for managing enchantments");
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        // Check permission
        if (context.getSender() instanceof Player) {
            Player player = (Player) context.getSender();
            if (!player.hasPermission("hyfactions.enchant.admin")) {
                context.sendMessage(Message.raw(plugin.getMessageUtil().getMessage("no-permission")));
                return CompletableFuture.completedFuture(null);
            }
        }

        // Show help if no args
        String[] args = context.getArgs();
        if (args.length == 0) {
            sendHelp(context);
            return CompletableFuture.completedFuture(null);
        }

        // Handle subcommands
        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(context);
            case "give" -> handleGive(context, args);
            case "remove" -> handleRemove(context, args);
            case "stats" -> handleStats(context, args);
            case "clear" -> handleClear(context, args);
            default -> sendHelp(context);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Handle /enchantadmin reload
     */
    private void handleReload(CommandContext context) {
        context.sendMessage(Message.raw("§6Reloading HyFactions-Enchanter..."));

        try {
            plugin.reloadPlugin();
            context.sendMessage(Message.raw("§aSuccessfully reloaded configuration!"));
        } catch (Exception e) {
            context.sendMessage(Message.raw("§cError reloading plugin: " + e.getMessage()));
            plugin.getLogger().error("Error reloading plugin", e);
        }
    }

    /**
     * Handle /enchantadmin give <player> <enchantment> <level>
     */
    private void handleGive(CommandContext context, String[] args) {
        if (args.length < 4) {
            context.sendMessage(Message.raw("§cUsage: /enchantadmin give <player> <enchantment> <level>"));
            return;
        }

        // TODO: Implement player lookup when Hytale API provides it
        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(Message.raw("§cThis command can only be run by players for now"));
            return;
        }

        Player player = (Player) context.getSender();
        String enchantId = args[2].toUpperCase();
        int level;

        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            context.sendMessage(Message.raw("§cInvalid level: " + args[3]));
            return;
        }

        // Get enchantment
        CustomEnchantment enchantment = plugin.getEnchantmentManager().getEnchantment(enchantId);
        if (enchantment == null) {
            context.sendMessage(Message.raw("§cInvalid enchantment: " + enchantId));
            return;
        }

        // Check level
        if (level < 1 || level > enchantment.getMaxLevel()) {
            context.sendMessage(Message.raw("§cInvalid level. Must be between 1 and " + enchantment.getMaxLevel()));
            return;
        }

        // Apply to held item
        player.getWorld().execute(() -> {
            ItemStack item = ItemUtil.getHeldItem(player);
            if (ItemUtil.isNullOrEmpty(item)) {
                context.sendMessage(Message.raw("§cYou must be holding an item!"));
                return;
            }

            // Apply enchantment
            ItemUtil.addEnchantment(item, enchantId, level);

            context.sendMessage(Message.raw("§aGave " + enchantment.getFormattedDisplayName() + " " +
                ItemUtil.getRomanNumeral(level) + " §ato held item"));
        });
    }

    /**
     * Handle /enchantadmin remove <player> <enchantment>
     */
    private void handleRemove(CommandContext context, String[] args) {
        if (args.length < 3) {
            context.sendMessage(Message.raw("§cUsage: /enchantadmin remove <player> <enchantment>"));
            return;
        }

        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(Message.raw("§cThis command can only be run by players for now"));
            return;
        }

        Player player = (Player) context.getSender();
        String enchantId = args[2].toUpperCase();

        player.getWorld().execute(() -> {
            ItemStack item = ItemUtil.getHeldItem(player);
            if (ItemUtil.isNullOrEmpty(item)) {
                context.sendMessage(Message.raw("§cYou must be holding an item!"));
                return;
            }

            if (!ItemUtil.hasEnchantment(item, enchantId)) {
                context.sendMessage(Message.raw("§cThis item doesn't have that enchantment!"));
                return;
            }

            // Remove enchantment
            ItemUtil.removeEnchantment(item, enchantId);

            context.sendMessage(Message.raw("§aRemoved " + enchantId + " from held item"));
        });
    }

    /**
     * Handle /enchantadmin stats [enchantment]
     */
    private void handleStats(CommandContext context, String[] args) {
        if (args.length < 2) {
            // Show global stats
            context.sendMessage(Message.raw("§6§lEnchantment Statistics"));
            context.sendMessage(Message.raw("§7Total enchantments: §e" +
                plugin.getEnchantmentManager().getEnchantmentIds().size()));

            // TODO: Add more global statistics when database methods are available
            context.sendMessage(Message.raw("§7Use §e/enchantadmin stats <enchantment> §7for specific stats"));
            return;
        }

        String enchantId = args[1].toUpperCase();
        CustomEnchantment enchantment = plugin.getEnchantmentManager().getEnchantment(enchantId);

        if (enchantment == null) {
            context.sendMessage(Message.raw("§cInvalid enchantment: " + enchantId));
            return;
        }

        // Display enchantment statistics
        context.sendMessage(Message.raw("§6§lStatistics - " + enchantment.getFormattedDisplayName()));
        context.sendMessage(Message.raw("§7ID: §e" + enchantment.getId()));
        context.sendMessage(Message.raw("§7Tier: " + enchantment.getTier().getDisplayName()));
        context.sendMessage(Message.raw("§7Category: " + enchantment.getCategory().getDisplayName()));
        context.sendMessage(Message.raw("§7Max Level: §e" + enchantment.getMaxLevel()));

        // Get usage statistics from database
        Map<String, Long> stats = plugin.getDatabaseManager().getEnchantmentStatistics(enchantId);
        context.sendMessage(Message.raw("§7Applications: §e" + stats.getOrDefault("applications", 0L)));
        context.sendMessage(Message.raw("§7Removals: §e" + stats.getOrDefault("removals", 0L)));
        context.sendMessage(Message.raw("§7Triggers: §e" + stats.getOrDefault("triggers", 0L)));
    }

    /**
     * Handle /enchantadmin clear <player>
     */
    private void handleClear(CommandContext context, String[] args) {
        if (args.length < 2) {
            context.sendMessage(Message.raw("§cUsage: /enchantadmin clear <player>"));
            return;
        }

        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(Message.raw("§cThis command can only be run by players for now"));
            return;
        }

        Player player = (Player) context.getSender();

        player.getWorld().execute(() -> {
            ItemStack item = ItemUtil.getHeldItem(player);
            if (ItemUtil.isNullOrEmpty(item)) {
                context.sendMessage(Message.raw("§cYou must be holding an item!"));
                return;
            }

            Map<String, Integer> enchantments = ItemUtil.getItemEnchantments(item);
            if (enchantments.isEmpty()) {
                context.sendMessage(Message.raw("§cThis item has no enchantments!"));
                return;
            }

            int count = enchantments.size();

            // Clear all enchantments
            ItemUtil.setItemEnchantments(item, Map.of());

            // Update database
            UUID playerUuid = PlayerUtil.getPlayerUUID(player);
            UUID itemUuid = ItemUtil.getItemUUID(item);

            for (String enchantId : enchantments.keySet()) {
                plugin.getDatabaseManager().removePlayerEnchantment(playerUuid, itemUuid, enchantId);
            }

            context.sendMessage(Message.raw("§aRemoved " + count + " enchantments from held item"));
        });
    }

    /**
     * Send help message
     */
    private void sendHelp(CommandContext context) {
        context.sendMessage(Message.raw("§6§lEnchantment Admin Commands"));
        context.sendMessage(Message.raw("§e/enchantadmin reload §7- Reload configuration"));
        context.sendMessage(Message.raw("§e/enchantadmin give <player> <enchant> <level> §7- Give enchantment"));
        context.sendMessage(Message.raw("§e/enchantadmin remove <player> <enchant> §7- Remove enchantment"));
        context.sendMessage(Message.raw("§e/enchantadmin stats [enchant] §7- View statistics"));
        context.sendMessage(Message.raw("§e/enchantadmin clear <player> §7- Clear all enchantments from item"));
    }
}
