package org.jballs.ballerelo;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {

    private final ballerelo plugin;
    private Connection connection;

    public DatabaseManager(ballerelo plugin) {
        this.plugin = plugin;
        connectSQLite();
    }

    private void connectSQLite() {
        plugin.getLogger().info("USING SQLITE");
        File dataFolder = plugin.getDataFolder();
        File dataFile = new File(dataFolder, "data.db");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String url = "jdbc:sqlite:" + dataFile;

        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(url);
            plugin.getLogger().info("Connected to SQLite database successfully");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            plugin.getLogger().info("Could not connect to SQLite database, disabling...");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
        init();
    }

    public void init() {
        String SQL = "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VCHAR(36) PRIMARY KEY, " +
                "elo INTEGER NOT NULL DEFAULT 0);";
        try {
            PreparedStatement ps = connection.prepareStatement(SQL);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerPlayer(String uuid) {
        String SQL = "INSERT OR IGNORE INTO player_data (uuid) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addElo(String uuid, int amount) {
        String SQL = "UPDATE player_data SET elo = elo + ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, amount);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getElo(String uuid) {
        String SQL = "SELECT elo FROM player_data WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("elo");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public Map<String, Integer> getEloMap() {
        Map<String, Integer> eloMap = new HashMap<>();
        String SQL = "SELECT uuid, elo FROM player_data";

        try (PreparedStatement ps = connection.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String uuid = rs.getString("uuid");
                int elo = rs.getInt("elo");
                eloMap.put(uuid, elo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return eloMap;
    }

    public void resetElo() {
        String SQL = "UPDATE player_data SET elo = 0";
        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
