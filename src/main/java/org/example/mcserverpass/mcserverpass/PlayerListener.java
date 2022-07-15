package org.example.mcserverpass.mcserverpass;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {
    private final MCServerPass plugin;

    PlayerListener(MCServerPass plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (plugin.getPlayersLoggedIn().contains(player.getUniqueId()))
            return;

        if (plugin.getLocation(player.getUniqueId()) == null)
            plugin.savePlayerData(player);

        plugin.getPlayersLoggingIn().add(player.getUniqueId());
        player.teleport(player.getLocation().add(0, 400, 0));
        player.setAllowFlight(true);
        var last_pos = plugin.getLocation(player.getUniqueId());
        if (last_pos != null)
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Last position: " + last_pos.getBlockX() +
                    ", " + last_pos.getBlockY() + ", " + last_pos.getBlockZ());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Login using the /login command!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();

        plugin.getPlayersLoggingIn().remove(event.getPlayer().getUniqueId());
        if (!plugin.getPlayersLoggedIn().contains(player.getUniqueId()))
            return;

        plugin.savePlayerData(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                plugin.getPlayersLoggedIn().remove(player.getUniqueId());
                plugin.getLogger().info("Logging out player " + player.getName());
            }
        }, plugin.getConfig().getLong("logoutTimeout"));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent event) {
        var player = event.getPlayer();
        plugin.savePlayerData(player);
        if (plugin.getPlayersLoggingIn().contains(player.getUniqueId())) {
            player.setAllowFlight(true);
            player.teleport(player.getLocation().add(0, 400, 0));
        }
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

    @EventHandler
    public void onPlayerPickupItem(PlayerAttemptPickupItemEvent event) {
        if (plugin.getPlayersLoggingIn().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (plugin.getPlayersLoggingIn().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (plugin.getPlayersLoggingIn().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        if (plugin.getPlayersLoggingIn().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (plugin.getPlayersLoggingIn().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInventoryInteract(InventoryClickEvent event) {
        if (plugin.getPlayersLoggingIn().contains(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAir(EntityAirChangeEvent event) {
        if (event.getEntity() instanceof Player player)
            if (plugin.getPlayersLoggingIn().contains(player.getUniqueId()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player)
            if (plugin.getPlayersLoggingIn().contains(player.getUniqueId()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player)
            if (plugin.getPlayersLoggingIn().contains(player.getUniqueId()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPotion(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player player)
            if (plugin.getPlayersLoggingIn().contains(player.getUniqueId()))
                event.setCancelled(true);
    }
}
