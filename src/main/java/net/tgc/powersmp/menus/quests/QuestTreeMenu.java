package net.tgc.powersmp.menus.quests;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.tgc.powersmp.quests.QuestStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QuestTreeMenu extends Menu {
    public QuestTreeMenu(Player player, QuestStorage storage) {
        this.setTitle("<yellow>Quests");
        this.setSize(9*3);

        ConfigurationSection questsSection = storage.getQuestsSection();
        if (questsSection == null) return;

        int slot = 10;
        for (String treeKey : questsSection.getKeys(false)) {
            String name = questsSection.getString(treeKey + ".name");
            Material material = Material.valueOf(questsSection.getString(treeKey + ".icon"));
            String description = questsSection.getString(treeKey + ".description");

            List<String> lore = List.of(
                    "<gray>" + description,
                    "",
                    "<yellow>→ <white>Click to view quests!"
            );

            this.addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    return ItemStackCreator.buildItem(material, name, lore);
                }

                @Override
                public void onClick(Player player) {
                    Bukkit.getScheduler().runTask(Main.getInstance(),
                            () -> new QuestPathMenu(player, storage, treeKey).displayTo(player));
                }
            });

            slot = slot + 2;

        }
    }
}
