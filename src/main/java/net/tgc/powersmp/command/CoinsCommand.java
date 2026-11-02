package net.tgc.powersmp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class CoinsCommand implements BasicCommand {

    private final Main plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final String PREFIX = "<grey>[<gold>Coins<grey>] <reset><white>";

    public CoinsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        // /coins — check own balance
        if (args.length == 0) {
            if (!(source.getSender() instanceof Player player)) {
                source.getSender().sendMessage("Console must specify a player.");
                return;
            }
            double balance = plugin.getPlayerData().getDouble("players." + player.getUniqueId() + ".coins", 0);
            player.sendMessage(mm.deserialize(PREFIX + "Your balance: <gold>" + (int) balance + " Coins"));
            return;
        }

        switch (args[0].toLowerCase()) {

            // /coins give <player> <amount>
            case "give" -> {
                if (!source.getSender().hasPermission("powersmp.coins.admin")) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>No permission."));
                    return;
                }
                if (args.length < 3) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Usage: /coins give <player> <amount>"));
                    return;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
                if (target == null) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Player not found."));
                    return;
                }
                int amount = Integer.parseInt(args[2]);
                String path = "players." + target.getUniqueId() + ".coins";
                double newBalance = plugin.getPlayerData().getDouble(path, 0) + amount;
                plugin.getPlayerData().set(path, newBalance);
                plugin.savePlayerData();
                source.getSender().sendMessage(mm.deserialize(PREFIX + "Gave <gold>" + amount + " Coins <white>to <gold>" + target.getName()));
                if (target.isOnline()) {
                    ((Player) target.getPlayer()).sendMessage(mm.deserialize(PREFIX + "You received <gold>" + amount + " Coins<white>!"));
                }
            }

            // /coins take <player> <amount>
            case "take" -> {
                if (!source.getSender().hasPermission("powersmp.coins.admin")) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>No permission."));
                    return;
                }
                if (args.length < 3) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Usage: /coins take <player> <amount>"));
                    return;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
                if (target == null) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Player not found."));
                    return;
                }
                int amount = Integer.parseInt(args[2]);
                String path = "players." + target.getUniqueId() + ".coins";
                double current = plugin.getPlayerData().getDouble(path, 0);
                if (current < amount) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Player doesn't have enough coins."));
                    return;
                }
                plugin.getPlayerData().set(path, current - amount);
                plugin.savePlayerData();
                source.getSender().sendMessage(mm.deserialize(PREFIX + "Took <gold>" + amount + " Coins <white>from <gold>" + target.getName()));
            }

            // /coins set <player> <amount>
            case "set" -> {
                if (!source.getSender().hasPermission("powersmp.coins.admin")) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>No permission."));
                    return;
                }
                if (args.length < 3) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Usage: /coins set <player> <amount>"));
                    return;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
                if (target == null) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Player not found."));
                    return;
                }
                int amount = Integer.parseInt(args[2]);
                plugin.getPlayerData().set("players." + target.getUniqueId() + ".coins", (double) amount);
                plugin.savePlayerData();
                source.getSender().sendMessage(mm.deserialize(PREFIX + "Set <gold>" + target.getName() + "<white>'s balance to <gold>" + amount + " Coins"));
            }

            // /coins balance <player> — check another player's balance
            case "balance" -> {
                if (!source.getSender().hasPermission("powersmp.coins.admin")) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>No permission."));
                    return;
                }
                if (args.length < 2) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Usage: /coins balance <player>"));
                    return;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
                if (target == null) {
                    source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Player not found."));
                    return;
                }
                double balance = plugin.getPlayerData().getDouble("players." + target.getUniqueId() + ".coins", 0);
                source.getSender().sendMessage(mm.deserialize(PREFIX + "<gold>" + target.getName() + "<white> has <gold>" + (int) balance + " Coins"));
            }

            default -> source.getSender().sendMessage(mm.deserialize(PREFIX + "<red>Usage: /coins [give|take|set|balance]"));
        }
    }
}
