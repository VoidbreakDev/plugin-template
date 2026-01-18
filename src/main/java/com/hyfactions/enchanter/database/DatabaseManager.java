package com.hyfactions.enchanter.database;

import com.hyfactions.enchanter.HyFactionsEnchanter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Manages database connections and operations
 * Supports both MySQL and SQLite
 */
public class DatabaseManager {

    private final HyFactionsEnchanter plugin;
    private HikariDataSource dataSource;
    private DatabaseType databaseType;

    public DatabaseManager(HyFactionsEnchanter plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize database connection
     */
    public void initialize() throws SQLException {
        String type = plugin.getConfigManager().getDatabaseType();
        this.databaseType = DatabaseType.valueOf(type.toUpperCase());

        plugin.getLogger().info("Initializing " + databaseType + " database...");

        HikariConfig config = new HikariConfig();

        switch (databaseType) {
            case MYSQL:
                setupMySQL(config);
                break;
            case SQLITE:
                setupSQLite(config);
                break;
        }

        dataSource = new HikariDataSource(config);

        // Create tables
        createTables();

        plugin.getLogger().info("Database initialized successfully.");
    }

    /**
     * Setup MySQL connection
     */
    private void setupMySQL(HikariConfig config) {
        String host = plugin.getConfigManager().getMysqlHost();
        int port = plugin.getConfigManager().getMysqlPort();
        String database = plugin.getConfigManager().getMysqlDatabase();
        String username = plugin.getConfigManager().getMysqlUsername();
        String password = plugin.getConfigManager().getMysqlPassword();

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Connection pool settings
        config.setMaximumPoolSize(plugin.getConfigManager().getMysqlMaxPoolSize());
        config.setMinimumIdle(plugin.getConfigManager().getMysqlMinIdle());
        config.setConnectionTimeout(plugin.getConfigManager().getMysqlConnectionTimeout());
        config.setIdleTimeout(plugin.getConfigManager().getMysqlIdleTimeout());
        config.setMaxLifetime(plugin.getConfigManager().getMysqlMaxLifetime());

        // Performance settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
    }

    /**
     * Setup SQLite connection
     */
    private void setupSQLite(HikariConfig config) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        String fileName = plugin.getConfigManager().getSqliteFileName();
        File dbFile = new File(dataFolder, fileName);

        config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1); // SQLite doesn't support multiple connections well
        config.setConnectionTestQuery("SELECT 1");
    }

    /**
     * Create database tables
     */
    private void createTables() throws SQLException {
        try (Connection conn = getConnection()) {
            // Player enchantments table
            String playerEnchantsSQL = databaseType == DatabaseType.SQLITE ?
                    "CREATE TABLE IF NOT EXISTS player_enchantments (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "player_uuid TEXT NOT NULL," +
                            "item_uuid TEXT NOT NULL," +
                            "enchantment_id TEXT NOT NULL," +
                            "enchantment_level INTEGER NOT NULL," +
                            "applied_at INTEGER NOT NULL," +
                            "UNIQUE(player_uuid, item_uuid, enchantment_id)" +
                            ")" :
                    "CREATE TABLE IF NOT EXISTS player_enchantments (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "player_uuid VARCHAR(36) NOT NULL," +
                            "item_uuid VARCHAR(36) NOT NULL," +
                            "enchantment_id VARCHAR(50) NOT NULL," +
                            "enchantment_level INT NOT NULL," +
                            "applied_at BIGINT NOT NULL," +
                            "UNIQUE KEY unique_enchant (player_uuid, item_uuid, enchantment_id)," +
                            "INDEX idx_player (player_uuid)," +
                            "INDEX idx_item (item_uuid)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

            conn.createStatement().execute(playerEnchantsSQL);

            // Player abilities table
            String playerAbilitiesSQL = databaseType == DatabaseType.SQLITE ?
                    "CREATE TABLE IF NOT EXISTS player_abilities (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "player_uuid TEXT NOT NULL," +
                            "ability_id TEXT NOT NULL," +
                            "unlocked_at INTEGER NOT NULL," +
                            "uses INTEGER DEFAULT 0," +
                            "UNIQUE(player_uuid, ability_id)" +
                            ")" :
                    "CREATE TABLE IF NOT EXISTS player_abilities (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "player_uuid VARCHAR(36) NOT NULL," +
                            "ability_id VARCHAR(50) NOT NULL," +
                            "unlocked_at BIGINT NOT NULL," +
                            "uses INT DEFAULT 0," +
                            "UNIQUE KEY unique_ability (player_uuid, ability_id)," +
                            "INDEX idx_player_ability (player_uuid)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

            conn.createStatement().execute(playerAbilitiesSQL);

            // Statistics table
            String statsSQL = databaseType == DatabaseType.SQLITE ?
                    "CREATE TABLE IF NOT EXISTS enchantment_statistics (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "enchantment_id TEXT NOT NULL," +
                            "total_applications INTEGER DEFAULT 0," +
                            "total_removals INTEGER DEFAULT 0," +
                            "total_uses INTEGER DEFAULT 0," +
                            "UNIQUE(enchantment_id)" +
                            ")" :
                    "CREATE TABLE IF NOT EXISTS enchantment_statistics (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "enchantment_id VARCHAR(50) NOT NULL UNIQUE," +
                            "total_applications BIGINT DEFAULT 0," +
                            "total_removals BIGINT DEFAULT 0," +
                            "total_uses BIGINT DEFAULT 0," +
                            "INDEX idx_enchant_stats (enchantment_id)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

            conn.createStatement().execute(statsSQL);

            plugin.getLogger().info("Database tables created successfully.");
        }
    }

    /**
     * Get a database connection
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not initialized!");
        }
        return dataSource.getConnection();
    }

    /**
     * Save player enchantment data
     */
    public CompletableFuture<Void> savePlayerEnchantment(UUID playerUuid, UUID itemUuid,
                                                          String enchantId, int level) {
        return CompletableFuture.runAsync(() -> {
            String sql = databaseType == DatabaseType.SQLITE ?
                    "INSERT OR REPLACE INTO player_enchantments (player_uuid, item_uuid, enchantment_id, enchantment_level, applied_at) VALUES (?, ?, ?, ?, ?)" :
                    "INSERT INTO player_enchantments (player_uuid, item_uuid, enchantment_id, enchantment_level, applied_at) " +
                            "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE enchantment_level = ?, applied_at = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerUuid.toString());
                stmt.setString(2, itemUuid.toString());
                stmt.setString(3, enchantId);
                stmt.setInt(4, level);
                stmt.setLong(5, System.currentTimeMillis());

                if (databaseType == DatabaseType.MYSQL) {
                    stmt.setInt(6, level);
                    stmt.setLong(7, System.currentTimeMillis());
                }

                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save player enchantment: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Remove player enchantment data
     */
    public CompletableFuture<Void> removePlayerEnchantment(UUID playerUuid, UUID itemUuid, String enchantId) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM player_enchantments WHERE player_uuid = ? AND item_uuid = ? AND enchantment_id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerUuid.toString());
                stmt.setString(2, itemUuid.toString());
                stmt.setString(3, enchantId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to remove player enchantment: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Get player's enchantments for an item
     */
    public CompletableFuture<Map<String, Integer>> getPlayerEnchantments(UUID playerUuid, UUID itemUuid) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> enchantments = new HashMap<>();
            String sql = "SELECT enchantment_id, enchantment_level FROM player_enchantments WHERE player_uuid = ? AND item_uuid = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerUuid.toString());
                stmt.setString(2, itemUuid.toString());

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    enchantments.put(rs.getString("enchantment_id"), rs.getInt("enchantment_level"));
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to get player enchantments: " + e.getMessage());
                e.printStackTrace();
            }

            return enchantments;
        });
    }

    /**
     * Increment enchantment statistics
     */
    public void incrementStatistic(String enchantId, StatisticType type) {
        CompletableFuture.runAsync(() -> {
            String column = switch (type) {
                case APPLICATIONS -> "total_applications";
                case REMOVALS -> "total_removals";
                case USES -> "total_uses";
            };

            String sql = databaseType == DatabaseType.SQLITE ?
                    "INSERT INTO enchantment_statistics (enchantment_id, " + column + ") VALUES (?, 1) " +
                            "ON CONFLICT(enchantment_id) DO UPDATE SET " + column + " = " + column + " + 1" :
                    "INSERT INTO enchantment_statistics (enchantment_id, " + column + ") VALUES (?, 1) " +
                            "ON DUPLICATE KEY UPDATE " + column + " = " + column + " + 1";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, enchantId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to update statistics: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Shutdown database connections
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            plugin.getLogger().info("Closing database connections...");
            dataSource.close();
        }
    }

    private enum DatabaseType {
        MYSQL, SQLITE
    }

    public enum StatisticType {
        APPLICATIONS, REMOVALS, USES
    }
}
