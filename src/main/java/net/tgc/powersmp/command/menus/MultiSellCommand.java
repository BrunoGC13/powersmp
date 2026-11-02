package net.tgc.powersmp.command.menus;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.MultiSellMenu;
import net.tgc.powersmp.menus.OrderMenu;
import net.tgc.powersmp.sell.SellManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class MultiSellCommand implements BasicCommand {
    private Main plugin;

    public MultiSellCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player p)) {
            source.getSender().sendRichMessage(
                    "<red>Only players can use this command.</red>"
            );
            return;
        }

        openMultiSellMenu(p);
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission(permission());
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.commands.multi_sell";
    }

    private void openMultiSellMenu(Player p) {
        new MultiSellMenu(plugin).displayTo(p);
    }
}
