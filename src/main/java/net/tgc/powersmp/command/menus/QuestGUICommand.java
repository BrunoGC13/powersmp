package net.tgc.powersmp.command.menus;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.quests.QuestTreeMenu;
import net.tgc.powersmp.quests.QuestStorage;
import org.bukkit.entity.Player;

public class QuestGUICommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) return;

        QuestStorage storage = Main.getInstance().getQuestStorage();
        new QuestTreeMenu(player, storage).displayTo(player);
    }

    @Override
    public @javax.annotation.Nullable String permission() {
        return "powersmp.commands.quest.opengui";
    }
}
