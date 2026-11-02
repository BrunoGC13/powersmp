package net.tgc.powersmp.dungeon;

import net.tgc.powersmp.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ArrayList;

public class DungeonPlayerData {
    private final Main plugin;

    public DungeonPlayerData(Main plugin) {
        this.plugin = plugin;
    }

    // ── Step ────────────────────────────────────────────────

    public int getStep(Player player, String dungeonId) {
        return plugin.getDungeonProgressData().getInt(key(player) + ".dungeons." + dungeonId + ".step", 0);
    }

    public void setStep(Player player, String dungeonId, int step) {
        String path = key(player) + ".dungeons." + dungeonId + ".step";
        plugin.getDungeonProgressData().set(path, step);
        save();
    }

    // ── Mined ───────────────────────────────────────────────

    public int getMined(Player player, String materialId) {
        return plugin.getDungeonProgressData().getInt(key(player) + ".mined." + materialId, 0);
    }

    public void incrementMined(Player player, String materialId, int amount) {
        String path = key(player) + ".mined." + materialId;
        int current = plugin.getDungeonProgressData().getInt(path, 0);
        plugin.getDungeonProgressData().set(path, current + amount);
        save();
    }

    // ── Kills ───────────────────────────────────────────────

    public int getKills(Player player, String mobId) {
        return plugin.getDungeonProgressData().getInt(key(player) + ".killed." + mobId, 0);
    }

    public void incrementKills(Player player, String mobId) {
        String path = key(player) + ".killed." + mobId;
        int current = plugin.getDungeonProgressData().getInt(path, 0);
        plugin.getDungeonProgressData().set(path, current + 1);
        save();
    }

    // ── Crafted ─────────────────────────────────────────────

    public int getCrafted(Player player, String itemId) {
        return plugin.getDungeonProgressData().getInt(key(player) + ".crafted." + itemId, 0);
    }

    public void incrementCrafted(Player player, String itemId) {
        String path = key(player) + ".crafted." + itemId;
        int current = plugin.getDungeonProgressData().getInt(path, 0);
        plugin.getDungeonProgressData().set(path, current + 1);
        save();
    }

    // ── Completed Objectives ─────────────────────────────────

    public boolean isObjectiveCompleted(Player player, String objectiveId) {
        return plugin.getDungeonProgressData()
                .getStringList(key(player) + ".completed_objectives")
                .contains(objectiveId);
    }

    public void completeObjective(Player player, String objectiveId) {
        String path = key(player) + ".completed_objectives";
        List<String> list = new ArrayList<>(
                plugin.getDungeonProgressData().getStringList(path)
        );
        if (!list.contains(objectiveId)) {
            list.add(objectiveId);
            plugin.getDungeonProgressData().set(path, list);
            save();
        }
    }

    // ── Helpers ──────────────────────────────────────────────

    private FileConfiguration getData(Player player) {
        return plugin.getDungeonProgressData();
    }

    private String key(Player player) {
        return "players." + player.getUniqueId();
    }

    private void save() {
        plugin.saveDungeonProgressData();
    }
}