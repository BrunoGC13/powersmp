package net.tgc.powersmp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.other.tpa.TPAType;
import net.tgc.powersmp.other.tpa.TPAManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TPARequestCommand implements BasicCommand {
    private final TPAType type;
    private final String permission;
    private final String prefix = Main.getInstance().getConfig().getString("plugin-prefix");

    public TPARequestCommand(TPAType type) {
        this.type = type;
        this.permission = type == TPAType.TPA
                ? "powersmp.commands.tpa"
                : "powersmp.commands.tpahere";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player sender)) {
            source.getSender().sendRichMessage(
                    "<red>Only players can use this command.</red>"
            );
            return;
        }

        if (args.length < 1) {
            sender.sendRichMessage(
                    type == TPAType.TPA
                            ? "<red>Usage: /tpa <player></red>"
                            : "<red>Usage: /tpahere <player></red>"
            );
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            sender.sendRichMessage(prefix + "<red>That player is not online.</red>");
            return;
        }

        if (target.equals(sender)) {
            sender.sendRichMessage(prefix + "<red>You cannot send a request to yourself.</red>");
            return;
        }

        TPAManager tpaManager = Main.getInstance().getTpaManager();
        tpaManager.sendRequest(sender, target, type);

        sender.sendRichMessage(
                type == TPAType.TPA
                        ? prefix + "<white>Teleport request sent to <green>" + target.getName() + "."
                        : prefix + "<white>Teleport-here request sent to <green>" + target.getName() + "."
        );
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length != 1) {
            return List.of();
        }

        List<String> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }

        return players;
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission(permission) || sender.isOp();
    }

    @Override
    public @Nullable String permission() {
        return permission;
    }
}