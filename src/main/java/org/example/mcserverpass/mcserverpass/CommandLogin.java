package org.example.mcserverpass.mcserverpass;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandLogin implements CommandExecutor {
    private final MCServerPass plugin;

    CommandLogin(MCServerPass plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (plugin.getPlayersLoggingIn().contains(player.getUniqueId())) {
                if (String.join(" ", args).equals("simp gaming")) {
                    player.sendMessage(ChatColor.GREEN + "Logging in!");
                    plugin.getLogger().info("Player " + player.getName() + " logged in!");
                    plugin.getPlayersLoggingIn().remove(player.getUniqueId());
                    plugin.getPlayersLoggedIn().add(player.getUniqueId());
                    if (player.getGameMode() == GameMode.SURVIVAL)
                        player.setAllowFlight(false);

                    var location = plugin.getLocation(player.getUniqueId());
                    player.teleport(Objects.requireNonNullElseGet(location,
                            () -> plugin.getServer().getWorlds().get(0).getSpawnLocation()));

                    var velocity = plugin.getVelocity(player.getUniqueId());
                    if (velocity != null)
                        player.setVelocity(velocity);
                } else
                    player.sendMessage(ChatColor.RED + "Incorrect password!");
            } else
                player.sendMessage(ChatColor.DARK_PURPLE + "You are already logged in!");

            return true;
        }
        return false;
    }
}
