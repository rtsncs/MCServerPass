package org.example.mcserverpass.mcserverpass;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.sql.SQLException;

public class PlayerListener implements Listener {
    private final MCServerPass plugin;

    PlayerListener(MCServerPass plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (plugin.getPlayersLoggedIn().contains(player.getUniqueId())) {
            return;
        }

        plugin.getPlayersLoggingIn().add(player.getUniqueId());
        player.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation().add(0.0,400.0,0.0));
        player.setAllowFlight(true);
        plugin.getLogger().info("Player " + player.getName() + " logging in!");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Login using the /login command!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) throws SQLException {
        var player = event.getPlayer();
        // TODO: keep the player logged in for some time after they leave

        plugin.getPlayersLoggingIn().remove(event.getPlayer().getUniqueId());
        if (!plugin.getPlayersLoggedIn().contains(player.getUniqueId()))
            return;

        var query = "MERGE INTO PLAYERS KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        var statement = plugin.con.prepareStatement(query);
        statement.setObject(1, player.getUniqueId());
        var location = player.isDead() ? plugin.getServer().getWorlds().get(0).getSpawnLocation()
                : player.getLocation();
        statement.setString(2, location.getWorld().getName());
        statement.setDouble(3, location.getX());
        statement.setDouble(4, location.getY());
        statement.setDouble(5, location.getZ());
        statement.setFloat(6, location.getPitch());
        statement.setFloat(7, location.getYaw());
        statement.execute();

        plugin.getPlayersLoggedIn().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player)
            if (plugin.getPlayersLoggingIn().contains(player.getUniqueId()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.getPlayersLoggingIn().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (plugin.getPlayersLoggingIn().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }
}
