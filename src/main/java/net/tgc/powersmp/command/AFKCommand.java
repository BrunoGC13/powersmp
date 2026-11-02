package net.tgc.powersmp.command;

import de.nikey.combatLog.Combat.CombatManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public class AFKCommand implements BasicCommand {
    private final Main plugin;
    private final String prefix;
    private CombatManager combat;
    private final String adminPerm = "powersmp.commands.afk.admin";
    private MiniMessage mm = MiniMessage.miniMessage();

    public AFKCommand(Main plugin, CombatManager combat) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
        this.combat = combat;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player sender)) {
            source.getSender().sendRichMessage(
                    "<red>Only players can use this command.</red>"
            );
            return;
        }

        if (combat.isInCombat(sender)) {
            sender.sendMessage(mm.deserialize(prefix + "<red>You cannot use this command while in combat!"));
            return;
        }

        if (args.length == 0) {
            handleAfk(sender);
        } else {
            handleAfkByAdmin(sender, args[0]);
        }
    }

    private void handleAfkByAdmin(Player sender, String player) {
        if (!sender.hasPermission(adminPerm)) {
            handleAfk(sender);
            return;
        }
        Player target = Bukkit.getPlayer(player);
        handleAfk(target);

        if (plugin.getAfkManager().checkPlayer(target)) {
            sender.sendMessage(mm.deserialize(prefix + "<white>Turned AFK for " + player + " <green>on."));
        } else {
            sender.sendMessage(mm.deserialize(prefix + "<white>Turned AFK for " + player + " <red>off."));
        }
    }

    private void handleAfk(Player sender) {
        plugin.getAfkManager().setAfkPlayer(sender, !plugin.getAfkManager().checkPlayer(sender));
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission(permission());
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.commands.afk";
    }
}
