package org.example.mcserverpass.mcserverpass;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandSetPassword implements CommandExecutor {
    private final MCServerPass plugin;

    public CommandSetPassword(MCServerPass plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.getConfig().set("password", String.join(" ", args));
        plugin.saveConfig();
        return true;
    }
}
