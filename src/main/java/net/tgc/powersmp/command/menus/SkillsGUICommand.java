package net.tgc.powersmp.command.menus;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.skills.SkillsMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class SkillsGUICommand implements BasicCommand {
    private Main plugin;
    public SkillsGUICommand(Main plugin) {
        this.plugin = plugin;
    }
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) return;
        new SkillsMenu(plugin).displayTo(player);

    }

    @Override
    public @Nullable String permission() {
        return "powersmp.command.menus.skills_menu";
    }
}
