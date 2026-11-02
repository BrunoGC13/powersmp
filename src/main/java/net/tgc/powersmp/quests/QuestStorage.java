package net.tgc.powersmp.quests;

import net.tgc.powersmp.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class QuestStorage {
    private final Main plugin;

    public QuestStorage(Main plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration quests() {
        return plugin.getQuestData();
    }

    public FileConfiguration players() {
        return plugin.getPlayerData();
    }

    public ConfigurationSection getQuestsSection() {
        return plugin.getQuestData().getConfigurationSection("quests");
    }

    public ConfigurationSection getTreeQuests(String treeKey) {
        return plugin.getQuestData().getConfigurationSection("quests." + treeKey + ".quests");
    }

    public ConfigurationSection getQuestAction(String treeKey, String questId) {
        return plugin.getQuestData().getConfigurationSection("quests." + treeKey + ".quests." + questId + ".action");
    }

    public String getQuestName(String treeKey, String questId) {
        return plugin.getQuestData().getString("quests." + treeKey + ".quests." + questId + ".name");
    }

    public int getQuestRewardAmount(String treeKey, String questId) {
        return plugin.getQuestData().getInt("quests." + treeKey + ".quests." + questId + ".reward");
    }

    public String getQuestRewardMaterial(String treeKey, String questId) {
        return plugin.getQuestData().getString("quests." + treeKey + ".quests." + questId + ".reward_material");
    }

    public String getActiveQuest(String uuid, String treeKey) {
        return plugin.getPlayerData().getString("players." + uuid + ".quests." + treeKey + ".active_quest");
    }

    public int getProgress(String uuid, String treeKey) {
        return plugin.getPlayerData().getInt("players." + uuid + ".quests." + treeKey + ".progress");
    }

    public void setActiveQuest(String uuid, String treeKey, String questId) {
        plugin.getPlayerData().set("players." + uuid + ".quests." + treeKey + ".active_quest", questId);
    }

    public void setProgress(String uuid, String treeKey, int progress) {
        plugin.getPlayerData().set("players." + uuid + ".quests." + treeKey + ".progress", progress);
    }

    public boolean hasActiveQuest(String uuid, String treeKey) {
        return plugin.getPlayerData().contains("players." + uuid + ".quests." + treeKey + ".active_quest");
    }

    public void save() {
        plugin.savePlayerData();
    }

    public String getQuestRewardCustomItem(String treeKey, String questId) {
        return plugin.getQuestData().getString("quests." + treeKey + ".quests." + questId + ".reward_custom_item");
    }
}