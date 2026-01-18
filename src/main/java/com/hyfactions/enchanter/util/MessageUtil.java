package com.hyfactions.enchanter.util;

import com.hyfactions.enchanter.HyFactionsEnchanter;

/**
 * Utility class for sending formatted messages to players
 */
public class MessageUtil {

    private final HyFactionsEnchanter plugin;
    private final String prefix;

    public MessageUtil(HyFactionsEnchanter plugin) {
        this.plugin = plugin;
        this.prefix = colorize(getConfigString("messages.prefix", "&8[&6HyEnchanter&8] &r"));
    }

    /**
     * Send a message to a player with prefix
     */
    public void sendMessage(Object player, String key, Object... replacements) {
        String message = getMessage(key);
        if (message == null || message.isEmpty()) {
            return;
        }

        // Apply replacements
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", String.valueOf(replacements[i + 1]));
            }
        }

        // Send to player (adapt to Hytale API)
        // ((Player) player).sendMessage(prefix + colorize(message));
    }

    /**
     * Send a raw message without prefix
     */
    public void sendRaw(Object player, String message) {
        // ((Player) player).sendMessage(colorize(message));
    }

    /**
     * Get a message from config
     */
    public String getMessage(String key) {
        return colorize(getConfigString("messages." + key, key));
    }

    /**
     * Get a message with replacements
     */
    public String getMessage(String key, Object... replacements) {
        String message = getMessage(key);

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", String.valueOf(replacements[i + 1]));
            }
        }

        return message;
    }

    /**
     * Colorize a string (convert color codes)
     */
    public String colorize(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "§");
    }

    /**
     * Strip color codes from a string
     */
    public String stripColor(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("§[0-9a-fk-or]", "");
    }

    private String getConfigString(String path, String def) {
        // return plugin.getConfig().getString(path, def);
        return def;
    }
}
