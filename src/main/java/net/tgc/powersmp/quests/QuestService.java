package net.tgc.powersmp.quests;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.levelSystem.LevelManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class QuestService {
    private final Main plugin;
    private final QuestStorage storage;
    private final LevelManager levelManager;

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String PREFIX = "<grey>[<yellow>Quests<grey>] <reset><white>";

    public QuestService(Main plugin, QuestStorage storage, LevelManager manager) {
        this.plugin = plugin;
        this.storage = storage;
        this.levelManager = manager;
    }

    public void initPlayer(Player player) {
        String uuid = player.getUniqueId().toString();

        ConfigurationSection questsSection = storage.getQuestsSection();
        if (questsSection == null) return;

        for (String treeKey : questsSection.getKeys(false)) {
            if (storage.hasActiveQuest(uuid, treeKey)) continue;

            ConfigurationSection treeQuests = storage.getTreeQuests(treeKey);
            if (treeQuests == null) continue;

            String firstQuestKey = treeQuests.getKeys(false).stream().findFirst().orElse(null);
            if (firstQuestKey == null) continue;

            storage.setActiveQuest(uuid, treeKey, firstQuestKey);
            storage.setProgress(uuid, treeKey, 0);
        }

        storage.save();
    }

    public Map<String, ConfigurationSection> getActiveQuestsPerTree(Player player) {
        String uuid = player.getUniqueId().toString();
        Map<String, ConfigurationSection> activeQuests = new HashMap<>();

        ConfigurationSection questsSection = storage.getQuestsSection();
        if (questsSection == null) return activeQuests;

        for (String treeKey : questsSection.getKeys(false)) {
            String currentQuestId = storage.getActiveQuest(uuid, treeKey);
            if (currentQuestId == null || currentQuestId.equals("COMPLETED")) continue;

            ConfigurationSection action = storage.getQuestAction(treeKey, currentQuestId);
            if (action == null) continue;

            activeQuests.put(treeKey, action);
        }

        return activeQuests;
    }

    public void handleProgress(Player player, String treeKey, ConfigurationSection action, String actionType, String materialName) {
        if (!action.getString("type", "").equalsIgnoreCase(actionType)) return;
        if (!action.getString("material", "").equalsIgnoreCase(materialName)) return;

        String uuid = player.getUniqueId().toString();
        int current = storage.getProgress(uuid, treeKey);
        int required = action.getInt("amount");

        storage.setProgress(uuid, treeKey, current + 1);
        storage.save();

        if (current + 1 >= required) {
            String currentQuestId = storage.getActiveQuest(uuid, treeKey);
            completeQuest(player, uuid, treeKey, currentQuestId);
        }
    }

    private void completeQuest(Player player, String uuid, String treeKey, String completedQuestId) {
        String completedQuestName = storage.getQuestName(treeKey, completedQuestId);
        int rewardAmount = storage.getQuestRewardAmount(treeKey, completedQuestId);
        String customItemKey = storage.getQuestRewardCustomItem(treeKey, completedQuestId);

        String rewardDisplay;
        if (customItemKey != null) {
            NamespacedKey key = NamespacedKey.fromString(customItemKey);
            ItemStack rewardItem = CustomItemRegistry.getInstance().createItemStack(key);
            rewardItem.setAmount(rewardAmount);
            player.getInventory().addItem(rewardItem);
            rewardDisplay = customItemKey;
        } else {
            Material rewardMaterial = Material.valueOf(storage.getQuestRewardMaterial(treeKey, completedQuestId));
            player.getInventory().addItem(new ItemStack(rewardMaterial, rewardAmount));
            rewardDisplay = rewardMaterial.name();
        }

        ConfigurationSection treeQuests = storage.getTreeQuests(treeKey);
        List<String> questIds = new ArrayList<>(treeQuests.getKeys(false));
        int nextIndex = questIds.indexOf(completedQuestId) + 1;

        player.sendMessage(MM.deserialize(PREFIX + "Quest completed: <green>" + completedQuestName));
        player.sendMessage(MM.deserialize(PREFIX + "Reward: <gray>" + rewardAmount + "x " + rewardDisplay));

        if (nextIndex < questIds.size()) {
            String nextQuestId = questIds.get(nextIndex);
            String nextQuestName = storage.getQuestName(treeKey, nextQuestId);

            storage.setActiveQuest(uuid, treeKey, nextQuestId);
            storage.setProgress(uuid, treeKey, 0);

            player.sendMessage(MM.deserialize(PREFIX + "Next Quest: <green>" + nextQuestName));
        } else {
            storage.setActiveQuest(uuid, treeKey, "COMPLETED");
            storage.setProgress(uuid, treeKey, 0);

            player.sendMessage(MM.deserialize(PREFIX + "You've completed the entire <yellow>" + treeKey + " <white>quest tree!"));
        }

        levelManager.addXp(player, 10);

        storage.save();
    }
}