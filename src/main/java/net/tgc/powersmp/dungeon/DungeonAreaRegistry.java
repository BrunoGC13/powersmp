package net.tgc.powersmp.dungeon;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.dungeon.unlock.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class DungeonAreaRegistry {
    private final Main plugin;

    public DungeonAreaRegistry(Main plugin) {
        this.plugin = plugin;
    }

    // ── Dungeon laden ────────────────────────────────────────

    public Dungeon loadDungeon(String dungeonName) {
        ConfigurationSection d = plugin.getDungeonsData()
                .getConfigurationSection("dungeons." + dungeonName);
        if (d == null) return null;

        World world = Bukkit.getWorld(Objects.requireNonNull(d.getString("world")));

        // center direkt als Section
        Location center = loadLocation(d, "center", world);

        // music ist verschachtelt: music.musicStart
        Location music = null;
        ConfigurationSection musicSection = d.getConfigurationSection("music");
        if (musicSection != null) {
            music = loadLocation(musicSection, "musicStart", world);
        }

        double musicStartRadius = d.getDouble("music.musicStartRadius", 6.0);
        String musicId          = d.getString("music.music", "");
        String boss             = d.getString("boss_mob", "");
        int cooldown            = d.getInt("boss_cooldown_seconds", 600);
        double radius           = d.getDouble("trigger_radius", 250);

        return new Dungeon(world, dungeonName, center, music,
                musicStartRadius, musicId, boss, cooldown, radius);
    }

    // ── Area registrieren ────────────────────────────────────

    public void registerArea(DungeonArea area) {
        String base = "areas." + area.getDungeon().getName() + "." + area.getName();
        plugin.getAreasData().set(base + ".name", area.getName());
        plugin.getAreasData().set(base + ".id", area.getId());
        plugin.getAreasData().set(base + ".dungeon", area.getDungeon().getName());

        saveLocation(base + ".border1", area.getBorder1());
        saveLocation(base + ".border2", area.getBorder2());
        saveLocation(base + ".pos1", area.getPos1());
        saveLocation(base + ".pos2", area.getPos2());

        plugin.saveDungeonAreasData();
    }

    // ── Area laden ───────────────────────────────────────────

    public DungeonArea getArea(String areaName) {
        ConfigurationSection allDungeons = plugin.getAreasData().getConfigurationSection("areas");
        if (allDungeons == null) return null;

        for (String dungeonKey : allDungeons.getKeys(false)) {
            ConfigurationSection dungeonSection = allDungeons.getConfigurationSection(dungeonKey);
            if (dungeonSection == null) continue;

            for (String areaKey : dungeonSection.getKeys(false)) {
                ConfigurationSection area = dungeonSection.getConfigurationSection(areaKey);
                if (area == null) continue;
                if (!Objects.equals(area.getString("name"), areaName)) continue;

                return buildArea(area);
            }
        }
        return null;
    }

    // Lädt Area anhand von dungeonId + step (id)
    public DungeonArea getAreaByStep(String dungeonId, int step) {
        ConfigurationSection dungeonSection = plugin.getAreasData()
                .getConfigurationSection("areas." + dungeonId);
        if (dungeonSection == null) return null;

        for (String areaKey : dungeonSection.getKeys(false)) {
            ConfigurationSection area = dungeonSection.getConfigurationSection(areaKey);
            if (area == null) continue;
            if (area.getInt("id") == step) return buildArea(area);
        }
        return null;
    }

    // Alle Areas eines Dungeons
    public List<DungeonArea> getAllAreas(String dungeonId) {
        List<DungeonArea> result = new ArrayList<>();
        ConfigurationSection dungeonSection = plugin.getAreasData()
                .getConfigurationSection("areas." + dungeonId);
        if (dungeonSection == null) return result;

        for (String areaKey : dungeonSection.getKeys(false)) {
            ConfigurationSection area = dungeonSection.getConfigurationSection(areaKey);
            if (area != null) result.add(buildArea(area));
        }
        result.sort(Comparator.comparingInt(DungeonArea::getId));
        return result;
    }

    // ── Hilfsmethoden ────────────────────────────────────────

    private DungeonArea buildArea(ConfigurationSection area) {
        String dungeonName = area.getString("dungeon");
        Dungeon dungeon = loadDungeon(dungeonName);

        Location border1 = loadLocation(area, "border1");
        Location border2 = loadLocation(area, "border2");
        Location pos1    = loadLocation(area, "pos1");
        Location pos2    = loadLocation(area, "pos2");

        DungeonArea dungeonArea = new DungeonArea(
                area.getString("name"),
                area.getInt("id"),
                border1, border2,
                pos1, pos2,
                dungeon
        );

        // Conditions laden
        List<UnlockCondition> conditions = loadConditions(area);
        dungeonArea.setUnlockConditions(conditions);

        return dungeonArea;
    }

    private List<UnlockCondition> loadConditions(ConfigurationSection area) {
        List<UnlockCondition> conditions = new ArrayList<>();
        if (!area.isList("unlock_conditions")) return conditions;

        for (Map<?, ?> rawEntry : area.getMapList("unlock_conditions")) {
            // Cast to a usable map type
            Map<String, Object> entry = new HashMap<>();
            rawEntry.forEach((k, v) -> entry.put(k.toString(), v));

            String type = (String) entry.get("type");
            if (type == null) continue;

            switch (type) {
                case "OBJECTIVE_COMPLETE" -> {
                    String id = (String) entry.get("objective_id");
                    int finalStep = ((Number) entry.getOrDefault("final_step", 1)).intValue();
                    if (id != null) conditions.add(new ObjectiveCondition(plugin, id, finalStep));
                }
                case "MATERIAL_MINED" -> {
                    String id = (String) entry.get("material_id");
                    int amount = ((Number) entry.getOrDefault("amount", 1)).intValue();
                    if (id != null) conditions.add(new MaterialMinedCondition(plugin, id, amount));
                }
                case "MOB_KILLED" -> {
                    String id = (String) entry.get("mob_id");
                    int amount = ((Number) entry.getOrDefault("amount", 1)).intValue();
                    if (id != null) conditions.add(new MobKilledCondition(plugin, id, amount));
                }
                case "ITEM_CRAFTED" -> {
                    String id = (String) entry.get("item_id");
                    int amount = ((Number) entry.getOrDefault("amount", 1)).intValue();
                    if (id != null) conditions.add(new ItemCraftedCondition(plugin, id, amount));
                }
            }
        }
        return conditions;
    }

    private void saveLocation(String path, Location loc) {
        plugin.getAreasData().set(path + ".world", loc.getWorld().getName());
        plugin.getAreasData().set(path + ".x", loc.getX());
        plugin.getAreasData().set(path + ".y", loc.getY());
        plugin.getAreasData().set(path + ".z", loc.getZ());
    }

    private Location loadLocation(
            ConfigurationSection section,
            String key,
            World world
    ) {
        ConfigurationSection loc = section.getConfigurationSection(key);
        if (loc == null) return null;

        return new Location(
                world,
                loc.getDouble("x"),
                loc.getDouble("y"),
                loc.getDouble("z")
        );
    }

    private Location loadLocation(ConfigurationSection section, String key) {
        ConfigurationSection loc = section.getConfigurationSection(key);
        if (loc == null) return null;

        String worldName = loc.getString("world");
        if (worldName == null || worldName.isBlank()) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        return new Location(
                world,
                loc.getDouble("x"),
                loc.getDouble("y"),
                loc.getDouble("z")
        );
    }
}