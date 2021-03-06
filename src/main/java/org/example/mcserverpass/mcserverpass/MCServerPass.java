package org.example.mcserverpass.mcserverpass;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class MCServerPass extends JavaPlugin {
    private final List<UUID> playersLoggingIn = new LinkedList<>();
    private final List<UUID> playersLoggedIn = new LinkedList<>();

    public MCServerPass() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            getLogger().severe("Error setting up the database!");
            getServer().shutdown();
            throw new RuntimeException(e);
        }
        try (var con = getDBConnection(); var statement = con.createStatement()) {
            var query = "CREATE TABLE IF NOT EXISTS players (" +
                    "id UUID, world varchar, x float, y float, z float, pitch float, yaw float, vx float, vy float, vz float)";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            getLogger().severe("Error setting up the database!");
            getServer().shutdown();
            throw new RuntimeException(e);
        }
    }

    public Connection getDBConnection() {
        try {
            return DriverManager.getConnection("jdbc:h2:file:./serverPass");
        } catch (SQLException e) {
            getLogger().severe("Error getting database connection!");
            getServer().shutdown();
            throw new RuntimeException(e);
        }
    }

    public List<UUID> getPlayersLoggingIn() {
        return playersLoggingIn;
    }

    public List<UUID> getPlayersLoggedIn() {
        return playersLoggedIn;
    }

    @Override
    public void onEnable() {
        var config = getConfig();
        config.addDefault("password", "password");
        config.addDefault("logoutTimeout", 1200);
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Objects.requireNonNull(getCommand("login")).setExecutor(new CommandLogin(this));
        Objects.requireNonNull(getCommand("setpassword")).setExecutor(new CommandSetPassword(this));
    }

    public Location getLocation(UUID player) {
        var query = "SELECT * FROM PLAYERS WHERE id = ?";
        try (var con = getDBConnection(); var statement = con.prepareStatement(query)) {
            statement.setObject(1, player);
            var result = statement.executeQuery();
            if (result.next()) {
                return new Location(
                        getServer().getWorld(result.getString(2)),
                        result.getDouble(3),
                        result.getDouble(4),
                        result.getDouble(5),
                        result.getFloat(7),
                        result.getFloat(6)
                );
            } else
                return null;
        } catch (SQLException e) {
            getLogger().severe("Error getting location for player " + player + ": " + e.getMessage());
            return null;
        }
    }

    public Vector getVelocity(UUID player) {
        var query = "SELECT * FROM PLAYERS WHERE id = ?";
        try (var con = getDBConnection(); var statement = con.prepareStatement(query)) {
            statement.setObject(1, player);
            var result = statement.executeQuery();
            if (result.next()) {
                return new Vector(
                        result.getDouble(8),
                        result.getDouble(9),
                        result.getDouble(10)
                );
            } else
                return null;
        } catch (SQLException e) {
            getLogger().severe("Error getting velocity for player " + player + ": " + e.getMessage());
            return null;
        }
    }

    public void savePlayerData(Player player) {
        getLogger().info("Saving player data for: " + player.getName());
        var query = "MERGE INTO PLAYERS KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var con = getDBConnection(); var statement = con.prepareStatement(query)) {
            statement.setObject(1, player.getUniqueId());
            var location = player.isDead() ? getServer().getWorlds().get(0).getSpawnLocation()
                    : player.getLocation();
            statement.setString(2, location.getWorld().getName());
            statement.setDouble(3, location.getX());
            statement.setDouble(4, location.getY());
            statement.setDouble(5, location.getZ());
            statement.setFloat(6, location.getPitch());
            statement.setFloat(7, location.getYaw());
            var velocity = player.isDead() ? new Vector(0.0, 0.0, 0.0) : player.getVelocity();
            statement.setDouble(8, velocity.getX());
            statement.setDouble(9, velocity.getY());
            statement.setDouble(10, velocity.getZ());
            statement.execute();
        } catch (SQLException e) {
            getLogger().severe("Error saving player data for: " + player.getName() + ": " + e.getMessage());
        }
    }
}
