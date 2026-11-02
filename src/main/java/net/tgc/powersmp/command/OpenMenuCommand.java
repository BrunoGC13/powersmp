package net.tgc.powersmp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.menus.MenuRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class OpenMenuCommand implements BasicCommand {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String PREFIX = "<grey>[<gold>PowerSMP<grey>] <reset><white>";

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (args.length == 0) {
            sender.sendMessage(MM.deserialize(PREFIX + "<red>Usage: <gray>/openmenu <menu> [player]"));
            sender.sendMessage(MM.deserialize(PREFIX + "Menus: <gray>" + String.join(", ", MenuRegistry.getIds())));
            return;
        }

        String menuId = args[0].toLowerCase();
        if (!MenuRegistry.exists(menuId)) {
            sender.sendMessage(MM.deserialize(PREFIX + "<red>Unknown menu: <gray>" + menuId));
            sender.sendMessage(MM.deserialize(PREFIX + "Available: <gray>" + String.join(", ", MenuRegistry.getIds())));
            return;
        }

        // Resolve viewer — must be a player
        if (!(sender instanceof Player viewer)) {
            sender.sendMessage(MM.deserialize(PREFIX + "<red>Console cannot open menus. Specify a target player."));
            return;
        }

        // Resolve target — optional second argument, requires extra permission
        Player target;
        if (args.length >= 2) {
            if (!sender.hasPermission("powersmp.menu.open.other")) {
                sender.sendMessage(MM.deserialize(PREFIX + "<red>You don't have permission to open menus for other players."));
                return;
            }
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MM.deserialize(PREFIX + "<red>Player not found: <gray>" + args[1]));
                return;
            }
        } else {
            target = viewer;
        }

        MenuRegistry.open(menuId, viewer, target);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length == 1) {
            return filter(MenuRegistry.getIds(), args[0]);
        }
        if (args.length == 2 && source.getSender().hasPermission("powersmp.menu.open.other")) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), args[1]);
        }
        return List.of();
    }

    private static List<String> filter(List<String> options, String input) {
        if (input.isEmpty()) return options;
        String lower = input.toLowerCase();
        return options.stream().filter(s -> s.toLowerCase().startsWith(lower)).toList();
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.command.menu.open";
    }
}
