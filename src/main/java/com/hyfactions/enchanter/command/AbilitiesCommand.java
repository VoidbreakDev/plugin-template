package com.hyfactions.enchanter.command;

import com.hyfactions.enchanter.HyFactionsEnchanter;
import com.hyfactions.enchanter.ability.MagicalAbility;
import com.hyfactions.enchanter.ability.AbilityType;
import com.hyfactions.enchanter.util.PlayerUtil;

// Hytale API imports
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.text.Message;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Abilities command handler
 * /abilities [player] - view and manage magical abilities
 */
public class AbilitiesCommand extends AbstractCommand {

    private final HyFactionsEnchanter plugin;

    public AbilitiesCommand(HyFactionsEnchanter plugin) {
        super("abilities", "Manage player magical abilities");
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
        if (!player.hasPermission("hyfactions.abilities.use")) {
            PlayerUtil.sendMessage(player, plugin.getMessageUtil().getMessage("no-permission"));
            return CompletableFuture.completedFuture(null);
        }

        String[] args = context.getArgs();

        // Handle viewing abilities
        if (args.length == 0) {
            // Show player's own abilities
            showAbilities(player, player);
            return CompletableFuture.completedFuture(null);
        }

        // Handle viewing another player's abilities
        if (args.length == 1) {
            if (!player.hasPermission("hyfactions.abilities.viewothers")) {
                PlayerUtil.sendMessage(player, plugin.getMessageUtil().getMessage("no-permission"));
                return CompletableFuture.completedFuture(null);
            }

            // TODO: Get player by name when Hytale API provides player lookup
            // For now, just show own abilities
            PlayerUtil.sendMessage(player, "&cPlayer lookup not yet implemented");
            return CompletableFuture.completedFuture(null);
        }

        sendHelp(player);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Display abilities for a player
     */
    private void showAbilities(Player viewer, Player target) {
        UUID targetUuid = PlayerUtil.getPlayerUUID(target);
        String targetName = PlayerUtil.getName(target);

        PlayerUtil.sendMessage(viewer, "&6&lMagical Abilities - " + targetName);
        PlayerUtil.sendMessage(viewer, "");

        // Get unlocked abilities from database
        Set<String> unlockedAbilities = plugin.getDatabaseManager().getPlayerAbilities(targetUuid);

        if (unlockedAbilities.isEmpty()) {
            PlayerUtil.sendMessage(viewer, "&7No abilities unlocked yet");
            PlayerUtil.sendMessage(viewer, "&7Use enchantments to unlock abilities!");
            return;
        }

        // Display abilities by type
        displayAbilitiesByType(viewer, unlockedAbilities, AbilityType.ACTIVE, "&c&lActive Abilities");
        displayAbilitiesByType(viewer, unlockedAbilities, AbilityType.PASSIVE, "&a&lPassive Abilities");
        displayAbilitiesByType(viewer, unlockedAbilities, AbilityType.COMBO, "&e&lCombo Abilities");

        PlayerUtil.sendMessage(viewer, "");
        PlayerUtil.sendMessage(viewer, "&7Total: &e" + unlockedAbilities.size() + " abilities");
    }

    /**
     * Display abilities of a specific type
     */
    private void displayAbilitiesByType(Player viewer, Set<String> unlockedAbilities,
                                        AbilityType type, String header) {
        List<MagicalAbility> abilities = plugin.getAbilityManager().getAbilitiesByType(type);

        // Filter to only unlocked abilities
        List<MagicalAbility> unlocked = abilities.stream()
            .filter(ability -> unlockedAbilities.contains(ability.getId()))
            .toList();

        if (unlocked.isEmpty()) {
            return;
        }

        PlayerUtil.sendMessage(viewer, "");
        PlayerUtil.sendMessage(viewer, header);

        for (MagicalAbility ability : unlocked) {
            int level = plugin.getDatabaseManager().getAbilityLevel(
                PlayerUtil.getPlayerUUID(viewer),
                ability.getId()
            );

            String cooldown = ability.getCooldown() > 0
                ? " &8(CD: " + ability.getCooldown() + "s)"
                : "";

            PlayerUtil.sendMessage(viewer,
                "&7• &e" + ability.getId() + " " + getRomanNumeral(level) + cooldown);

            // Show description
            String description = ability.getDescription().size() > 0
                ? ability.getDescription().get(0)
                : "";
            if (!description.isEmpty()) {
                PlayerUtil.sendMessage(viewer, "  &7" + description);
            }
        }
    }

    /**
     * Send help message
     */
    private void sendHelp(Player player) {
        PlayerUtil.sendMessage(player, "&6&lAbilities Commands");
        PlayerUtil.sendMessage(player, "&e/abilities &7- View your abilities");
        PlayerUtil.sendMessage(player, "&e/abilities <player> &7- View another player's abilities");
    }

    /**
     * Convert number to Roman numeral
     */
    private String getRomanNumeral(int number) {
        if (number < 1 || number > 10) return String.valueOf(number);
        String[] numerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return numerals[number];
    }
}
