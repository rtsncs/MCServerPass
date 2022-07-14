package org.example.mcserverpass.mcserverpass;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
public final class MCServerPass extends JavaPlugin {
    private final List<UUID> playersLoggingIn = new ArrayList<>();
    private final List<UUID> playersLoggedIn = new ArrayList<>();
    public Connection con;

    public List<UUID> getPlayersLoggingIn() {
        return playersLoggingIn;
    }

    public List<UUID> getPlayersLoggedIn() {
        return playersLoggedIn;
    }

    public MCServerPass() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        con = DriverManager.getConnection("jdbc:h2:file:./serverPass");
        var query = "CREATE TABLE IF NOT EXISTS players (id UUID, world varchar, x float, y float, z float, pitch float, yaw float)";
        var statement = con.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Objects.requireNonNull(getCommand("login")).setExecutor(new CommandLogin(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Location getLocation(UUID player) throws SQLException {
        var query = "SELECT * FROM PLAYERS WHERE id = ?";
        var statement = con.prepareStatement(query);
        statement.setObject(1, player);
        var result = statement.executeQuery();
        if (result.next()) {
            var location = new Location(
                    getServer().getWorld(result.getString(2)),
                    result.getDouble(3),
                    result.getDouble(4),
                    result.getDouble(5),
                    result.getFloat(7),
                    result.getFloat(6)
            );
            statement.close();
            return location;
        } else {
            return null;
        }
    }
}
