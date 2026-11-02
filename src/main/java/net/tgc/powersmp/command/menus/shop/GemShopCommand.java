package net.tgc.powersmp.command.menus.shop;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.npcs.GemMasterMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class GemShopCommand implements BasicCommand {
    private Main plugin;
    public GemShopCommand(Main plugin) {
        this.plugin = plugin;
    }
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) return;
        if (!(source.getExecutor() instanceof Player p)) {
            return;
        }
        new GemMasterMenu(p, plugin.getCoinsEconomy()).displayTo(player);
    }
    @Override
    public @Nullable String permission() {
        return "powersmp.commands.menus.gem_master_menu";
    }
}
