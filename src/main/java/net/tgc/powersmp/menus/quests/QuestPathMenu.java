package net.tgc.powersmp.menus.quests;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.quests.QuestStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.tgc.powersmp.other.ItemStackCreator.buildItem;

public class QuestPathMenu extends Menu {
    public QuestPathMenu(Player player, QuestStorage storage, String treeKey) {
        this.setTitle("<yellow>Quest Path");
        this.setSize(9 * 3);

        String uuid = player.getUniqueId().toString();
        String activeQuestId = storage.getActiveQuest(uuid, treeKey);
        int currentProgress = storage.getProgress(uuid, treeKey);
        ConfigurationSection treeQuests = storage.getTreeQuests(treeKey);
        if (treeQuests == null) return;

        List<String> questIds = new ArrayList<>(treeQuests.getKeys(false));
        int activeIndex = questIds.indexOf(activeQuestId);

        int slot = 10;
        for (int i = 0; i < questIds.size(); i++) {
            String questId = questIds.get(i);
            String questName = storage.getQuestName(treeKey, questId);
            int rewardAmount = storage.getQuestRewardAmount(treeKey, questId);

            // Resolve reward display — custom item takes priority over vanilla material
            String customItemKey = storage.getQuestRewardCustomItem(treeKey, questId);
            String rewardDisplay = customItemKey != null
                    ? customItemKey
                    : storage.getQuestRewardMaterial(treeKey, questId);

            ConfigurationSection action = storage.getQuestAction(treeKey, questId);
            String actionType = action != null ? action.getString("type", "?") : "?";
            String actionMaterial = action != null ? action.getString("material", "?") : "?";
            int actionAmount = action != null ? action.getInt("amount") : 0;

            boolean isCompleted = activeIndex == -1 || i < activeIndex;
            boolean isCurrent = i == activeIndex;

            Material pane;
            String stateLabel;

            if (isCompleted) {
                pane = Material.LIME_STAINED_GLASS_PANE;
                stateLabel = "<green>Completed!";
            } else if (isCurrent) {
                pane = Material.YELLOW_STAINED_GLASS_PANE;
                stateLabel = "<yellow>In progress";
            } else {
                pane = Material.RED_STAINED_GLASS_PANE;
                stateLabel = "<red>Locked";
            }

            List<String> lore = new ArrayList<>();
            lore.add(stateLabel);
            lore.add("");
            lore.add("<gray>Task: <white>" + actionType + " " + actionAmount + "x " + actionMaterial);

            if (isCurrent) {
                lore.add("<gray>Progress: <white>" + currentProgress + "/" + actionAmount);
            }

            lore.add("");
            lore.add("<gray>Reward: <white>" + rewardAmount + "x " + rewardDisplay);

            final int currentSlot = slot++;
            final Material finalPane = pane;
            final String finalQuestName = questName;
            final List<String> finalLore = lore;

            this.addButton(new Button(currentSlot) {
                @Override
                public ItemStack getItem() {
                    return buildItem(finalPane, "<white>" + finalQuestName, finalLore);
                }

                @Override
                public void onClick(Player player) {}
            });
        }

        // Back button
        this.addButton(new Button(9 * 3 - 1) {
            @Override
            public ItemStack getItem() {
                return buildItem(Material.ARROW, "<gray>Back", List.of("<gray>Return to quest trees"));
            }

            @Override
            public void onClick(Player player) {
                Bukkit.getScheduler().runTask(Main.getInstance(),
                        () -> new QuestTreeMenu(player, storage).displayTo(player));
            }
        });
    }
}