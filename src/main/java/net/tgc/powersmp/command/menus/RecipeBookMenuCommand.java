package net.tgc.powersmp.command.menus;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.recipes.RecipeBookMenu;
import net.tgc.powersmp.menus.shop.StrongholdShopMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class RecipeBookMenuCommand implements BasicCommand {
    private Main plugin;
    public RecipeBookMenuCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) return;
        new RecipeBookMenu(plugin).displayTo(player);
    }

    @Override
    public @Nullable String permission() {
        return "powersmp.commands.menus.recipe_book";
    }
}
