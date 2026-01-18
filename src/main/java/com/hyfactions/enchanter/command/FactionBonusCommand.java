package com.hyfactions.enchanter.command;

import com.hyfactions.enchanter.HyFactionsEnchanter;
import com.hyfactions.enchanter.faction.FactionBonus;
import com.hyfactions.enchanter.util.PlayerUtil;

// Hytale API imports
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.text.Message;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Faction bonus command handler
 * /factionbonus [faction] - view faction enchantment bonuses
 */
public class FactionBonusCommand extends AbstractCommand {

    private final HyFactionsEnchanter plugin;

    public FactionBonusCommand(HyFactionsEnchanter plugin) {
        super("factionbonus", "View and manage faction enchantment bonuses");
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        // Check if sender is a player
        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(Message.raw(plugin.getMessageUtil().getMessage("player-only")));
            return CompletableFuture.completedFuture(null);
        }

        Player player = (Player) context.getSender();

        // Check permission
        if (!player.hasPermission("hyfactions.factionbonus.use")) {
            context.sendMessage(Message.raw(plugin.getMessageUtil().getMessage("no-permission")));
            return CompletableFuture.completedFuture(null);
        }

        String[] args = context.getArgs();

        if (args.length == 0) {
            // Show player's faction bonuses
            showOwnFactionBonus(context, player);
            return CompletableFuture.completedFuture(null);
        }

        // Show specific faction's bonuses
        String factionName = args[0];
        showFactionBonus(context, player, factionName);

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Show player's own faction bonuses
     */
    private void showOwnFactionBonus(CommandContext context, Player player) {
        // TODO: Integrate with actual faction system when available
        // For now, show general information
        context.sendMessage(Message.raw("&6&lFaction Enchantment Bonuses"));
        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("&7Faction bonuses enhance enchantments when in your territory!"));
        context.sendMessage(Message.raw(""));

        // Get all configured faction bonuses
        Map<String, FactionBonus> bonuses = plugin.getFactionBonusManager().getAllBonuses();

        if (bonuses.isEmpty()) {
            context.sendMessage(Message.raw("&cNo faction bonuses configured"));
            return;
        }

        context.sendMessage(Message.raw("&e&lAvailable Bonuses:"));
        context.sendMessage(Message.raw(""));

        for (FactionBonus bonus : bonuses.values()) {
            displayBonus(context, player, bonus);
        }

        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("&7Use &e/factionbonus <faction> &7to see specific faction bonuses"));
    }

    /**
     * Show specific faction's bonuses
     */
    private void showFactionBonus(CommandContext context, Player player, String factionName) {
        FactionBonus bonus = plugin.getFactionBonusManager().getBonus(factionName);

        if (bonus == null) {
            context.sendMessage(Message.raw("&cFaction bonus not found: " + factionName));
            context.sendMessage(Message.raw("&7Use /factionbonus to see all available bonuses"));
            return;
        }

        context.sendMessage(Message.raw("&6&lFaction Bonus - " + bonus.getId()));
        context.sendMessage(Message.raw(""));

        displayBonus(context, player, bonus);

        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("&7This bonus is active when you're in your faction territory"));
    }

    /**
     * Display a faction bonus
     */
    private void displayBonus(CommandContext context, Player player, FactionBonus bonus) {
        // Display bonus type
        context.sendMessage(Message.raw("&e" + bonus.getId() + " &7- " + bonus.getBonusType()));

        // Display affected enchantments
        if (!bonus.getAffectedEnchantments().isEmpty()) {
            context.sendMessage(Message.raw("  &7Affects: &e" +
                String.join(", ", bonus.getAffectedEnchantments())));
        }

        // Display multiplier
        context.sendMessage(Message.raw("  &7Multiplier: &a" +
            String.format("%.1f", bonus.getMultiplier()) + "x"));

        // Display required members if applicable
        if (bonus.getRequiredMembers() > 0) {
            context.sendMessage(Message.raw("  &7Required Members: &e" +
                bonus.getRequiredMembers()));
        }

        // Display description
        if (!bonus.getDescription().isEmpty()) {
            for (String line : bonus.getDescription()) {
                context.sendMessage(Message.raw("  &7" + line));
            }
        }
    }

    /**
     * Send help message
     */
    private void sendHelp(CommandContext context, Player player) {
        context.sendMessage(Message.raw("&6&lFaction Bonus Commands"));
        context.sendMessage(Message.raw("&e/factionbonus &7- View all faction bonuses"));
        context.sendMessage(Message.raw("&e/factionbonus <faction> &7- View specific faction bonus"));
    }
}
