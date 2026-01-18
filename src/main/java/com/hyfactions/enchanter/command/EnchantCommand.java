package com.hyfactions.enchanter.command;

import com.hyfactions.enchanter.HyFactionsEnchanter;
import com.hyfactions.enchanter.enchantment.CustomEnchantment;
import com.hyfactions.enchanter.enchantment.EnchantmentTier;
import com.hyfactions.enchanter.enchantment.EnchantmentCategory;
import com.hyfactions.enchanter.util.ItemUtil;
import com.hyfactions.enchanter.util.PlayerUtil;

// Hytale API imports
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Main enchantment command handler
 * /enchant <list|apply|remove|info> [args]
 */
public class EnchantCommand extends AbstractCommand {

    private final HyFactionsEnchanter plugin;

    public EnchantCommand(HyFactionsEnchanter plugin) {
        super("enchant", "Main enchantment command for HyFactions Enchanter");
        this.plugin = plugin;
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        // Check if sender is a player
        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(Message.raw(plugin.getMessageUtil().getMessage("player-only")));
            return CompletableFuture.completedFuture(null);
        }

        Player player = (Player) context.getSender();

        // Check permission
        if (!player.hasPermission("hyfactions.enchant.use")) {
            PlayerUtil.sendMessage(player, plugin.getMessageUtil().getMessage("no-permission"));
            return CompletableFuture.completedFuture(null);
        }

        String[] args = context.getArgs();

        // Show help if no args
        if (args.length == 0) {
            sendHelp(player);
            return CompletableFuture.completedFuture(null);
        }

        // Handle subcommands
        switch (args[0].toLowerCase()) {
            case "list" -> handleList(player, args);
            case "apply" -> handleApply(player, args);
            case "remove" -> handleRemove(player, args);
            case "info" -> handleInfo(player, args);
            default -> sendHelp(player);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Handle /enchant list [tier|category]
     */
    private void handleList(Player player, String[] args) {
        if (args.length >= 2) {
            // List by tier or category
            String filter = args[1].toUpperCase();

            // Try tier filter
            try {
                EnchantmentTier tier = EnchantmentTier.valueOf(filter);
                PlayerUtil.sendMessage(player, "&6&lEnchantments - " + tier.getDisplayName());

                plugin.getEnchantmentManager().getEnchantmentsByTier(tier).forEach(enchant -> {
                    PlayerUtil.sendMessage(player, "&7• &e" + enchant.getId() + " &7- " +
                            enchant.getFormattedDisplayName());
                });
                return;
            } catch (IllegalArgumentException ignored) {}

            // Try category filter
            try {
                EnchantmentCategory category = EnchantmentCategory.valueOf(filter);
                PlayerUtil.sendMessage(player, "&6&lEnchantments - " + category.getDisplayName());

                plugin.getEnchantmentManager().getEnchantmentsByCategory(category).forEach(enchant -> {
                    PlayerUtil.sendMessage(player, "&7• &e" + enchant.getId() + " &7- " +
                            enchant.getFormattedDisplayName());
                });
                return;
            } catch (IllegalArgumentException ignored) {}

            PlayerUtil.sendMessage(player, "&cInvalid filter! Use: COMMON, RARE, EPIC, LEGENDARY, COMBAT, UTILITY, FACTION");
            return;
        }

        // List all enchantments
        PlayerUtil.sendMessage(player, "&6&lAvailable Enchantments");
        PlayerUtil.sendMessage(player, "&7Use /enchant list <tier|category> to filter");
        PlayerUtil.sendMessage(player, "");

        plugin.getEnchantmentManager().getEnchantmentIds().stream()
                .limit(10)
                .forEach(id -> {
                    CustomEnchantment enchant = plugin.getEnchantmentManager().getEnchantment(id);
                    if (enchant != null) {
                        PlayerUtil.sendMessage(player, "&7• &e" + id + " &7- " +
                                enchant.getFormattedDisplayName() + " &8(" + enchant.getTier().getDisplayName() + "&8)");
                    }
                });
    }

    /**
     * Handle /enchant apply <enchantment> [level]
     */
    private void handleApply(Player player, String[] args) {
        if (args.length < 2) {
            PlayerUtil.sendMessage(player, "&cUsage: /enchant apply <enchantment> [level]");
            return;
        }

        String enchantId = args[1].toUpperCase();
        int level = args.length >= 3 ? parseLevel(args[2]) : 1;

        if (level <= 0) {
            PlayerUtil.sendMessage(player, "&cInvalid level!");
            return;
        }

        // Get enchantment
        CustomEnchantment enchantment = plugin.getEnchantmentManager().getEnchantment(enchantId);
        if (enchantment == null) {
            PlayerUtil.sendMessage(player, plugin.getMessageUtil().getMessage("invalid-enchantment"));
            return;
        }

        // Check level
        if (level > enchantment.getMaxLevel()) {
            PlayerUtil.sendMessage(player, "&cMax level for " + enchantId + " is " + enchantment.getMaxLevel());
            return;
        }

        // Check tier permission
        String tierPerm = "hyfactions.enchant.tier." + enchantment.getTier().name().toLowerCase();
        if (!player.hasPermission(tierPerm)) {
            PlayerUtil.sendMessage(player, "&cYou don't have access to " + enchantment.getTier().getDisplayName() + " &cenchantments!");
            return;
        }

        // Must execute on world thread for inventory access
        player.getWorld().execute(() -> {
            // Get held item
            ItemStack item = ItemUtil.getHeldItem(player);
            if (ItemUtil.isNullOrEmpty(item)) {
                PlayerUtil.sendMessage(player, plugin.getMessageUtil().getMessage("invalid-item"));
                return;
            }

            // Check if enchantment can be applied to this item
            if (!enchantment.canApplyTo(item.getType().toString())) {
                PlayerUtil.sendMessage(player, "&cCannot apply " + enchantId + " to this item!");
                return;
            }

            // Check max enchantments
            Map<String, Integer> currentEnchants = ItemUtil.getItemEnchantments(item);
            if (currentEnchants.size() >= plugin.getConfigManager().getMaxEnchantsPerItem() &&
                    !currentEnchants.containsKey(enchantId)) {
                PlayerUtil.sendMessage(player, plugin.getMessageUtil().getMessage("max-enchants-reached"));
                return;
            }

            // Check conflicts
            for (String existingEnchant : currentEnchants.keySet()) {
                if (enchantment.conflictsWith(existingEnchant)) {
                    PlayerUtil.sendMessage(player, "&c" + enchantId + " conflicts with " + existingEnchant + "!");
                    return;
                }
            }

            // Apply enchantment
            ItemUtil.addEnchantment(item, enchantId, level);

            // Save to database
            UUID playerUuid = PlayerUtil.getPlayerUUID(player);
            UUID itemUuid = ItemUtil.getItemUUID(item);
            plugin.getDatabaseManager().savePlayerEnchantment(playerUuid, itemUuid, enchantId, level);

            // Track statistics
            plugin.getDatabaseManager().incrementStatistic(enchantId,
                    com.hyfactions.enchanter.database.DatabaseManager.StatisticType.APPLICATIONS);

            // Success message
            PlayerUtil.sendMessage(player, plugin.getMessageUtil().getMessage("enchant-success",
                    "enchant", enchantment.getFormattedDisplayName()));
        });
    }

    /**
     * Handle /enchant remove <enchantment>
     */
    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            PlayerUtil.sendMessage(player, "&cUsage: /enchant remove <enchantment>");
            return;
        }

        String enchantId = args[1].toUpperCase();

        player.getWorld().execute(() -> {
            ItemStack item = ItemUtil.getHeldItem(player);
            if (ItemUtil.isNullOrEmpty(item)) {
                PlayerUtil.sendMessage(player, plugin.getMessageUtil().getMessage("invalid-item"));
                return;
            }

            if (!ItemUtil.hasEnchantment(item, enchantId)) {
                PlayerUtil.sendMessage(player, "&cThis item doesn't have that enchantment!");
                return;
            }

            // Remove enchantment
            ItemUtil.removeEnchantment(item, enchantId);

            // Update database
            UUID playerUuid = PlayerUtil.getPlayerUUID(player);
            UUID itemUuid = ItemUtil.getItemUUID(item);
            plugin.getDatabaseManager().removePlayerEnchantment(playerUuid, itemUuid, enchantId);

            // Track statistics
            plugin.getDatabaseManager().incrementStatistic(enchantId,
                    com.hyfactions.enchanter.database.DatabaseManager.StatisticType.REMOVALS);

            PlayerUtil.sendMessage(player, "&aRemoved " + enchantId + " from your item!");
        });
    }

    /**
     * Handle /enchant info <enchantment>
     */
    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            PlayerUtil.sendMessage(player, "&cUsage: /enchant info <enchantment>");
            return;
        }

        String enchantId = args[1].toUpperCase();
        CustomEnchantment enchantment = plugin.getEnchantmentManager().getEnchantment(enchantId);

        if (enchantment == null) {
            PlayerUtil.sendMessage(player, plugin.getMessageUtil().getMessage("invalid-enchantment"));
            return;
        }

        // Display info
        PlayerUtil.sendMessage(player, "&6&l" + enchantment.getFormattedDisplayName());
        PlayerUtil.sendMessage(player, "&7Tier: " + enchantment.getTier().getDisplayName());
        PlayerUtil.sendMessage(player, "&7Category: " + enchantment.getCategory().getDisplayName());
        PlayerUtil.sendMessage(player, "&7Max Level: &e" + enchantment.getMaxLevel());
        PlayerUtil.sendMessage(player, "");

        enchantment.getFormattedDescription().forEach(line ->
                PlayerUtil.sendMessage(player, line));

        if (!enchantment.getConflicts().isEmpty()) {
            PlayerUtil.sendMessage(player, "");
            PlayerUtil.sendMessage(player, "&cConflicts: " + String.join(", ", enchantment.getConflicts()));
        }

        if (!enchantment.getSynergies().isEmpty()) {
            PlayerUtil.sendMessage(player, "&aSynergies: " + String.join(", ", enchantment.getSynergies()));
        }
    }

    /**
     * Send help message
     */
    private void sendHelp(Player player) {
        PlayerUtil.sendMessage(player, "&6&lEnchantment Commands");
        PlayerUtil.sendMessage(player, "&e/enchant list [tier|category] &7- List enchantments");
        PlayerUtil.sendMessage(player, "&e/enchant apply <enchant> [level] &7- Apply to held item");
        PlayerUtil.sendMessage(player, "&e/enchant remove <enchant> &7- Remove from held item");
        PlayerUtil.sendMessage(player, "&e/enchant info <enchant> &7- View info");
    }

    /**
     * Parse level from string
     */
    private int parseLevel(String levelStr) {
        try {
            return Integer.parseInt(levelStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
