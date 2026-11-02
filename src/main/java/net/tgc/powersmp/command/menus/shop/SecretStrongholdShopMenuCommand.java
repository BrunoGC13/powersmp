package net.tgc.powersmp.command.menus.shop;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.shop.StrongholdShopMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class SecretStrongholdShopMenuCommand implements BasicCommand {
    private Main plugin;
    public SecretStrongholdShopMenuCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) return;
        new StrongholdShopMenu(plugin, plugin.getCoinsEconomy()).displayTo(player);
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.commands.menus.secretstrongholdshop";
    }
}
