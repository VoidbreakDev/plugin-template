package com.hyfactions.enchanter;

import com.hyfactions.enchanter.ability.AbilityManager;
import com.hyfactions.enchanter.command.*;
import com.hyfactions.enchanter.config.ConfigManager;
import com.hyfactions.enchanter.database.DatabaseManager;
import com.hyfactions.enchanter.enchantment.EnchantmentManager;
import com.hyfactions.enchanter.faction.FactionManager;
import com.hyfactions.enchanter.listener.*;
import com.hyfactions.enchanter.util.MessageUtil;

// Hytale API imports
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.logging.Logger;

/**
 * Main plugin class for HyFactions Enchanter
 * Comprehensive enchantment and magical abilities system for Hytale Factions
 *
 * @author HyFactions Team
 * @version 1.0.0
 */
@Getter
public class HyFactionsEnchanter extends JavaPlugin {

    @Getter
    private static HyFactionsEnchanter instance;

    // Core managers
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private EnchantmentManager enchantmentManager;
    private AbilityManager abilityManager;
    private FactionManager factionManager;
    private MessageUtil messageUtil;

    // Logger
    private final Logger logger;

    /**
     * Plugin constructor - receives JavaPluginInit from Hytale
     */
    public HyFactionsEnchanter(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;

        // Get logger
        this.logger = Logger.getLogger("HyFactionsEnchanter");
    }

    /**
     * Setup phase - Registration and initialization
     */
    @Override
    protected void setup() {
        long startTime = System.currentTimeMillis();

        logger.info("========================================");
        logger.info("  HyFactions Enchanter v1.0.0");
        logger.info("  Setting up...");
        logger.info("========================================");

        // Initialize configuration
        try {
            initializeConfiguration();
        } catch (Exception e) {
            logger.severe("Failed to initialize configuration!");
            e.printStackTrace();
            return;
        }

        // Initialize database
        try {
            initializeDatabase();
        } catch (Exception e) {
            logger.severe("Failed to initialize database!");
            e.printStackTrace();
            return;
        }

        // Initialize managers
        try {
            initializeManagers();
        } catch (Exception e) {
            logger.severe("Failed to initialize managers!");
            e.printStackTrace();
            return;
        }

        // Register listeners
        try {
            registerListeners();
        } catch (Exception e) {
            logger.severe("Failed to register listeners!");
            e.printStackTrace();
            return;
        }

        // Register commands
        try {
            registerCommands();
        } catch (Exception e) {
            logger.severe("Failed to register commands!");
            e.printStackTrace();
            return;
        }

        long loadTime = System.currentTimeMillis() - startTime;
        logger.info("========================================");
        logger.info("  Setup completed in " + loadTime + "ms");
        logger.info("  Enchantments: " + enchantmentManager.getEnchantmentCount());
        logger.info("  Abilities: " + abilityManager.getAbilityCount());
        logger.info("========================================");
    }

    /**
     * Initialize configuration files
     */
    private void initializeConfiguration() {
        logger.info("Loading configuration files...");

        configManager = new ConfigManager(this);
        configManager.load();

        messageUtil = new MessageUtil(this);

        logger.info("Configuration loaded successfully.");
    }

    /**
     * Initialize database connection
     */
    private void initializeDatabase() {
        logger.info("Initializing database...");

        databaseManager = new DatabaseManager(this);

        try {
            databaseManager.initialize();
            logger.info("Database initialized successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    /**
     * Initialize all managers
     */
    private void initializeManagers() {
        logger.info("Initializing managers...");

        // Initialize enchantment manager
        enchantmentManager = new EnchantmentManager(this);
        enchantmentManager.loadEnchantments();

        // Initialize ability manager
        abilityManager = new AbilityManager(this);
        abilityManager.loadAbilities();

        // Initialize faction manager (if factions are enabled)
        if (configManager.isFactionEnabled()) {
            factionManager = new FactionManager(this);
            factionManager.initialize();
        }

        logger.info("All managers initialized successfully.");
    }

    /**
     * Register event listeners
     */
    private void registerListeners() {
        logger.info("Registering event listeners...");

        // Register core listeners using Hytale EventRegistry
        new PlayerListener(this).register(this.getEventRegistry());
        new CombatListener(this).register(this.getEventRegistry());
        new BlockListener(this).register(this.getEventRegistry());
        new ItemListener(this).register(this.getEventRegistry());
        new AbilityListener(this).register(this.getEventRegistry());

        // Faction listeners (if enabled)
        if (configManager.isFactionEnabled()) {
            new FactionListener(this).register(this.getEventRegistry());
        }

        logger.info("Event listeners registered successfully.");
    }

    /**
     * Register plugin commands
     */
    private void registerCommands() {
        logger.info("Registering commands...");

        // Register commands using Hytale CommandRegistry
        this.getCommandRegistry().registerCommand(new EnchantCommand(this));
        this.getCommandRegistry().registerCommand(new AbilitiesCommand(this));
        this.getCommandRegistry().registerCommand(new FactionBonusCommand(this));
        this.getCommandRegistry().registerCommand(new EnchantAdminCommand(this));

        logger.info("Commands registered successfully.");
    }

    /**
     * Reload plugin configuration and data
     */
    public void reloadPlugin() {
        logger.info("Reloading HyFactions Enchanter...");

        // Reload configuration
        configManager.reload();

        // Reload enchantments
        enchantmentManager.reloadEnchantments();

        // Reload abilities
        abilityManager.reloadAbilities();

        logger.info("Plugin reloaded successfully.");
    }

    /**
     * Shutdown and cleanup
     */
    public void shutdown() {
        logger.info("Shutting down HyFactions Enchanter...");

        // Save all pending data
        if (abilityManager != null) {
            abilityManager.shutdown();
        }

        if (enchantmentManager != null) {
            enchantmentManager.shutdown();
        }

        // Close database connections
        if (databaseManager != null) {
            databaseManager.shutdown();
        }

        logger.info("HyFactions Enchanter disabled successfully.");
    }

    /**
     * Get plugin data folder
     */
    public File getDataFolder() {
        // Hytale will provide this path
        return new File("plugins/HyFactions-Enchanter");
    }
}
