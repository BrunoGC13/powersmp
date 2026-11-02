package net.tgc.powersmp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.civilization.Civilization;
import net.tgc.powersmp.civilization.CivilizationManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.civilization.CivilizationRegistry;
import net.tgc.powersmp.economy.CoinsEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CivilizationCommand implements BasicCommand {

    private final Main plugin;

    public CivilizationCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {

        CivilizationManager civManager = plugin.getCivilizationManager();
        CoinsEconomy eco = plugin.getCoinsEconomy();
        CivilizationRegistry civReg = plugin.getCivilizationRegistry();

        Player player = (source.getExecutor() instanceof Player p) ? p : null;

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "join" -> handleJoin(source, player, args, civManager);
            case "accept" -> handleAccept(source, player, args, civManager);
            case "create" -> handleCreate(source, player, args, civManager, eco, civReg);
            default -> source.getSender().sendRichMessage(
                    "<red>Unknown command.</red>"
            );
        }
    }

    private void handleCreate(CommandSourceStack source, Player p, String[] args, CivilizationManager civManager, CoinsEconomy eco, CivilizationRegistry civReg) {
        int civCost = plugin.getConfig().getInt("civilizations.creation-cost", 50000);
        if (p == null) {
            source.getSender().sendRichMessage("<red>Only players can use this command!</red>");
            return;
        }

        if (!(p.hasPermission("powersmp.command.civilization.create"))) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have the required permissions to perform this command!"));
            return;
        }

        if (args.length < 2) {
            p.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>Usage: /civilization create <name></red>"));
            return;
        }

        String civName = args[1];
        Civilization check = civManager.getCivilization(civName);
        if (check != null) {
            p.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>Civilization '" + civName + "' already exists!</red>"));
            return;
        }

        String existing = civManager.getPlayerCivilization(p.getUniqueId());
        if (existing != null) {
            p.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>You are already in a civilization: " + existing + "</red>"));
            return;
        }

        OfflinePlayer offP = Bukkit.getOfflinePlayer(p.getUniqueId());
        if (!(eco.has(offP, civCost))) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not meet the requirement of having " + civCost + " coins!"));
            return;
        }

        EconomyResponse ecoRes = eco.withdrawPlayer(offP, civCost);
        if (!(ecoRes.transactionSuccess())) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>An error occurred: ") + ecoRes.errorMessage);
            return;
        }

        Civilization civ = new Civilization(civName, p, p.getLocation());
        civReg.registerCivilization(civ);

        p.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("plugin-prefix") + "<white>Successfully created the civilization <green>") + civ.getName() + "!");
    }

    private void handleJoin(CommandSourceStack source, Player player, String[] args, CivilizationManager civManager) {
        if (player == null) {
            source.getSender().sendRichMessage("<red>Only players can use this command!</red>");
            return;
        }

        if (!(player.hasPermission("powersmp.command.civilization.join")))

        if (args.length < 2) {
            player.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>Usage: /civilization join <name></red>"));
            return;
        }

        String civName = args[1];

        Civilization civ = civManager.getCivilization(civName);
        if (civ == null) {
            player.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>Civilization '" + civName + "' does not exist!</red>"));
            return;
        }

        String existing = civManager.getPlayerCivilization(player.getUniqueId());
        if (existing != null) {
            player.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>You are already in a civilization: " + existing + "</red>"));
            return;
        }

        // Delegated fully to manager
        civManager.requestJoin(civ, player);
    }

    private void handleAccept(CommandSourceStack source, Player player, String[] args, CivilizationManager civManager) {
        if (player == null) {
            source.getSender().sendRichMessage("<red>Only players can use this command!</red>");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>Usage: /civilization accept <player></red>"));
            return;
        }

        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            player.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>Player not found: " + targetName + "</red>"));
            return;
        }

        String civName = civManager.getPlayerCivilization(player.getUniqueId());
        if (civName == null) {
            player.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>You are not in a civilization!</red>"));
            return;
        }

        Civilization civ = civManager.getCivilization(civName);
        if (civ == null) {
            player.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>Your civilization no longer exists!</red>"));
            return;
        }

        if (civ.getOwner() == null || !civ.getOwner().equals(player)) {
            player.sendMessage(MiniMessage.miniMessage()
                    .deserialize("<red>You are not the owner of this civilization!</red>"));
            return;
        }

        // Delegated fully to manager
        civManager.acceptJoinRequest(civ, target);

        player.sendMessage(MiniMessage.miniMessage()
                .deserialize("<green>You accepted " + target.getName() + " into your civilization!</green>"));
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.commands.civilization";
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        CivilizationManager civManager = plugin.getCivilizationManager();

        if (args.length == 1) {
            return List.of("join", "accept", "create");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            return civManager.getAllCivilizationNames();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            return List.of("<name>");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {
            List<String> players = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                players.add(p.getName());
            }
            return players;
        }

        return List.of();
    }

    @Override
    public boolean canUse(org.bukkit.command.CommandSender sender) {
        return sender.hasPermission("powersmp.commands.civilization") || sender.isOp();
    }
}