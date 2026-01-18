package com.hyfactions.enchanter.config;

import com.hyfactions.enchanter.HyFactionsEnchanter;
import lombok.Getter;

/**
 * Manages plugin configuration
 * Provides easy access to config values
 */
@Getter
public class ConfigManager {

    private final HyFactionsEnchanter plugin;

    // Database settings
    private String databaseType;
    private String sqliteFileName;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUsername;
    private String mysqlPassword;
    private int mysqlMaxPoolSize;
    private int mysqlMinIdle;
    private long mysqlConnectionTimeout;
    private long mysqlIdleTimeout;
    private long mysqlMaxLifetime;

    // Economy settings
    private boolean economyEnabled;
    private double commonCostMultiplier;
    private double rareCostMultiplier;
    private double epicCostMultiplier;
    private double legendaryCostMultiplier;
    private double applyBaseCost;
    private double removeBaseCost;
    private double combineBaseCost;

    // Enchantment settings
    private int maxEnchantsPerItem;
    private boolean allowStacking;
    private boolean checkConflicts;
    private boolean combinationBonusesEnabled;
    private double synergyMultiplier;

    // Ability settings
    private double globalCooldown;
    private boolean allowAbilitiesInCombat;
    private boolean manaSystemEnabled;
    private int maxMana;
    private double manaRegenRate;
    private double manaRegenDelay;

    // Faction settings
    private boolean factionEnabled;
    private double territoryDamageMultiplier;
    private double territoryDefenseMultiplier;
    private double territoryResourceMultiplier;
    private double raidAttackerDamage;
    private double raidDefenderDefense;

    // Effects settings
    private boolean effectsEnabled;
    private boolean soundsEnabled;
    private double soundVolume;
    private double soundPitch;

    // Performance settings
    private int cacheSize;
    private int cacheExpiry;
    private boolean asyncDatabase;
    private int updateInterval;

    // Admin settings
    private boolean debugMode;
    private boolean logApplications;
    private boolean trackStatistics;
    private boolean testingMode;

    public ConfigManager(HyFactionsEnchanter plugin) {
        this.plugin = plugin;
    }

    /**
     * Load configuration from file
     */
    public void load() {
        // In actual implementation, would load from config.yml
        // For now, using placeholder values

        // Database
        databaseType = getConfigString("database.type", "SQLITE");
        sqliteFileName = getConfigString("database.sqlite.file", "enchanter.db");
        mysqlHost = getConfigString("database.mysql.host", "localhost");
        mysqlPort = getConfigInt("database.mysql.port", 3306);
        mysqlDatabase = getConfigString("database.mysql.database", "hyfactions_enchanter");
        mysqlUsername = getConfigString("database.mysql.username", "root");
        mysqlPassword = getConfigString("database.mysql.password", "changeme");
        mysqlMaxPoolSize = getConfigInt("database.mysql.pool.maximum-pool-size", 10);
        mysqlMinIdle = getConfigInt("database.mysql.pool.minimum-idle", 2);
        mysqlConnectionTimeout = getConfigLong("database.mysql.pool.connection-timeout", 30000);
        mysqlIdleTimeout = getConfigLong("database.mysql.pool.idle-timeout", 600000);
        mysqlMaxLifetime = getConfigLong("database.mysql.pool.max-lifetime", 1800000);

        // Economy
        economyEnabled = getConfigBoolean("economy.enabled", true);
        commonCostMultiplier = getConfigDouble("economy.cost-multipliers.COMMON", 1.0);
        rareCostMultiplier = getConfigDouble("economy.cost-multipliers.RARE", 2.5);
        epicCostMultiplier = getConfigDouble("economy.cost-multipliers.EPIC", 5.0);
        legendaryCostMultiplier = getConfigDouble("economy.cost-multipliers.LEGENDARY", 10.0);
        applyBaseCost = getConfigDouble("economy.base-costs.apply", 1000);
        removeBaseCost = getConfigDouble("economy.base-costs.remove", 500);
        combineBaseCost = getConfigDouble("economy.base-costs.combine", 2000);

        // Enchantments
        maxEnchantsPerItem = getConfigInt("enchantments.max-enchantments-per-item", 5);
        allowStacking = getConfigBoolean("enchantments.allow-stacking", false);
        checkConflicts = getConfigBoolean("enchantments.check-conflicts", true);
        combinationBonusesEnabled = getConfigBoolean("enchantments.combination-bonuses.enabled", true);
        synergyMultiplier = getConfigDouble("enchantments.combination-bonuses.synergy-multiplier", 1.25);

        // Abilities
        globalCooldown = getConfigDouble("abilities.global-cooldown", 1.0);
        allowAbilitiesInCombat = getConfigBoolean("abilities.allow-in-combat", true);
        manaSystemEnabled = getConfigBoolean("abilities.mana-system.enabled", false);
        maxMana = getConfigInt("abilities.mana-system.max-mana", 100);
        manaRegenRate = getConfigDouble("abilities.mana-system.regen-rate", 1.0);
        manaRegenDelay = getConfigDouble("abilities.mana-system.regen-delay", 5.0);

        // Faction
        factionEnabled = getConfigBoolean("faction.enabled", true);
        territoryDamageMultiplier = getConfigDouble("faction.territory-bonus.damage-multiplier", 1.15);
        territoryDefenseMultiplier = getConfigDouble("faction.territory-bonus.defense-multiplier", 1.10);
        territoryResourceMultiplier = getConfigDouble("faction.territory-bonus.resource-multiplier", 1.20);
        raidAttackerDamage = getConfigDouble("faction.raid-bonus.attacker-damage", 1.10);
        raidDefenderDefense = getConfigDouble("faction.raid-bonus.defender-defense", 1.15);

        // Effects
        effectsEnabled = getConfigBoolean("effects.enabled", true);
        soundsEnabled = getConfigBoolean("effects.sounds.enabled", true);
        soundVolume = getConfigDouble("effects.sounds.volume", 0.5);
        soundPitch = getConfigDouble("effects.sounds.pitch", 1.0);

        // Performance
        cacheSize = getConfigInt("performance.cache-size", 1000);
        cacheExpiry = getConfigInt("performance.cache-expiry", 30);
        asyncDatabase = getConfigBoolean("performance.async-database", true);
        updateInterval = getConfigInt("performance.update-interval", 20);

        // Admin
        debugMode = getConfigBoolean("admin.debug", false);
        logApplications = getConfigBoolean("admin.log-applications", true);
        trackStatistics = getConfigBoolean("admin.track-statistics", true);
        testingMode = getConfigBoolean("admin.testing-mode", false);

        plugin.getLogger().info("Configuration loaded successfully.");
    }

    /**
     * Reload configuration
     */
    public void reload() {
        plugin.getLogger().info("Reloading configuration...");
        // plugin.reloadConfig();
        load();
    }

    // Placeholder config getters (would use actual config in real implementation)
    private String getConfigString(String path, String def) {
        // return plugin.getConfig().getString(path, def);
        return def;
    }

    private int getConfigInt(String path, int def) {
        // return plugin.getConfig().getInt(path, def);
        return def;
    }

    private long getConfigLong(String path, long def) {
        // return plugin.getConfig().getLong(path, def);
        return def;
    }

    private double getConfigDouble(String path, double def) {
        // return plugin.getConfig().getDouble(path, def);
        return def;
    }

    private boolean getConfigBoolean(String path, boolean def) {
        // return plugin.getConfig().getBoolean(path, def);
        return def;
    }
}
