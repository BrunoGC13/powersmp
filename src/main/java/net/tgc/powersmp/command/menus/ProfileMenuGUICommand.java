package net.tgc.powersmp.command.menus;

import de.nikey.combatLog.Combat.CombatManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.dialog.ProfileDialog;
import net.tgc.powersmp.menus.profiles.ProfileMenu;
import net.tgc.powersmp.menus.quests.QuestTreeMenu;
import net.tgc.powersmp.quests.QuestStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ProfileMenuGUICommand implements BasicCommand {
    private final Main plugin;
    private final String prefix;

    public ProfileMenuGUICommand(Main plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            new ProfileDialog(player, plugin, false).displayDialogTo(player);
            return;
        }

        final String playerName = String.join(" ", args);
        Player onlinePlayer = Bukkit.getPlayer(playerName);

        final Component noPlayerFound = MiniMessage.miniMessage().deserialize(
                prefix + "<red>No player found!"
        );
        if (onlinePlayer == null) {
            source.getSender().sendMessage(noPlayerFound);
            return;
        }
        new ProfileDialog(onlinePlayer, plugin, true).displayDialogTo(player);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length != 1) return List.of();
        String input = args[0].toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input))
                .toList();
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.menus.profile.open";
    }
}
