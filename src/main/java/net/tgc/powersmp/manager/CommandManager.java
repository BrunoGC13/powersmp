package net.tgc.powersmp.manager;

import net.tgc.powersmp.Main;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CommandManager {

    private final Main plugin;
    private final List<CommandEntry> pendingCommands = new ArrayList<>();

    public CommandManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(String name, BasicCommand command) {
        registerCommand(name, "", List.of(), command);
    }

    public void registerCommand(String name, String description, BasicCommand command) {
        registerCommand(name, description, List.of(), command);
    }

    public void registerCommand(String name, String description, List<String> aliases, BasicCommand command) {
        pendingCommands.add(new CommandEntry(name, description, aliases, command));
    }

    public void activate() {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            for (CommandEntry entry : pendingCommands) {
                event.registrar().register(entry.name(), entry.description(), entry.aliases(), entry.command());
            }
        });
    }

    private record CommandEntry(String name, String description, List<String> aliases, BasicCommand command) {}
}
