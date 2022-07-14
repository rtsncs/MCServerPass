package org.example.mcserverpass.mcserverpass;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class CommandLogin implements CommandExecutor {
    MCServerPass plugin;

    CommandLogin(MCServerPass plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (plugin.getPlayersLoggingIn().contains(player.getUniqueId())) {
                if (String.join(" ", args).equals("simp gaming")) {
                    player.sendMessage(ChatColor.GREEN + "Logging in!");
                    plugin.getPlayersLoggingIn().remove(player.getUniqueId());
                    plugin.getPlayersLoggedIn().add(player.getUniqueId());
                    player.setAllowFlight(false);
                    try {
                        var location = plugin.getLocation(player.getUniqueId());
                        if (location != null) {
                            player.teleport(location);
                        } else {
                            player.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Incorrect password!");
                }
            } else {
                player.sendMessage(ChatColor.DARK_PURPLE + "You are already logged in!");
            }
            return true;
        }
        return false;
    }
}
