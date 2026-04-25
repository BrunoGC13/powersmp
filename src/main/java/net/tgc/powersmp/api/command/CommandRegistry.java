package net.tgc.powersmp.api.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

public class CommandRegistry {
    /**
     * Registers a command at runtime. The command will be accessible as /<name> or /<pluginName>:<name>.
     *
     * @param plugin   the owning plugin
     * @param name     command name without /
     * @param executor the command handler
     */
    public static void register(Plugin plugin, String name, CommandExecutor executor) {
        register(plugin, name, executor, null);
    }

    /**
     * Registers a command with tab completion.
     */
    public static void register(Plugin plugin, String name, CommandExecutor executor, TabCompleter completer) {
        CommandMap commandMap = Bukkit.getServer().getCommandMap();
        commandMap.register(plugin.getName().toLowerCase(), new ApiCommand(plugin, name, executor, completer));
    }
}
