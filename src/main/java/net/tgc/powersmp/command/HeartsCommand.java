package net.tgc.powersmp.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.tgc.powersmp.player.HeartManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Stream;

/**
 * /hearts [player]
 * /hearts set|add|remove <player> <amount>   (requires powersmp.hearts.admin)
 */
public class HeartsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Usage: /hearts <player>", NamedTextColor.RED));
                return true;
            }
            showHearts(sender, player);
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED)); return true; }
            showHearts(sender, target);
            return true;
        }

        if (args.length == 3) {
            if (!sender.hasPermission("powersmp.hearts.admin")) {
                sender.sendMessage(Component.text("You don't have permission.", NamedTextColor.RED));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED)); return true; }
            int amount;
            try { amount = Integer.parseInt(args[2]); } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid number.", NamedTextColor.RED)); return true;
            }
            switch (args[0].toLowerCase()) {
                case "set"    -> HeartManager.setHearts(target, amount);
                case "add"    -> HeartManager.addHearts(target, amount);
                case "remove" -> HeartManager.removeHearts(target, amount);
                default -> { sender.sendMessage(Component.text("Unknown sub-command. Use set/add/remove.", NamedTextColor.RED)); return true; }
            }
            sender.sendMessage(Component.text("Updated hearts for ", NamedTextColor.GREEN)
                    .append(Component.text(target.getName(), NamedTextColor.GOLD))
                    .append(Component.text(". Now: " + HeartManager.getHearts(target) + " ❤", NamedTextColor.GREEN)));
            return true;
        }

        sender.sendMessage(Component.text("Usage: /hearts [player] | /hearts set|add|remove <player> <amount>", NamedTextColor.RED));
        return true;
    }

    private void showHearts(CommandSender sender, Player target) {
        int hearts = HeartManager.getHearts(target);
        sender.sendMessage(Component.text(target.getName() + " has ", NamedTextColor.GOLD)
                .append(Component.text(hearts + " ❤", NamedTextColor.RED))
                .append(Component.text(" hearts.", NamedTextColor.GOLD)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> players = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        if (args.length == 1) {
            return Stream.concat(Stream.of("set", "add", "remove"), players.stream())
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        if (args.length == 2) {
            return players.stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }
        return List.of();
    }
}
