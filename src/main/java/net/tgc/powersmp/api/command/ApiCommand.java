package net.tgc.powersmp.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ApiCommand extends Command {
    private final CommandExecutor executor;
    private final TabCompleter completer;

    public ApiCommand(Plugin plugin, String name, CommandExecutor executor, TabCompleter completer) {
        super(name);
        this.executor = executor;
        this.completer = completer;
        setPermissionMessage("§cYou don't have permission to use this command.");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return executor.onCommand(sender, this, label, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (completer != null) {
            List<String> result = completer.onTabComplete(sender, this, alias, args);
            if (result != null) return result;
        }
        return super.tabComplete(sender, alias, args);
    }
}
