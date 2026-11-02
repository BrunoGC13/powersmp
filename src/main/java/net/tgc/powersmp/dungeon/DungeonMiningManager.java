package net.tgc.powersmp.dungeon;

import net.tgc.powersmp.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class DungeonMiningManager implements Listener {

    private final Main plugin;
    private final List<MiningZone> zones = new ArrayList<>();
    private final Map<String, Material> pendingRespawn = new HashMap<>();

    public DungeonMiningManager(Main plugin) {
        this.plugin = plugin;
        loadZones();
        startRespawnTask();
    }

    private void loadZones() {
        ConfigurationSection dungeons = plugin.getDungeonsData()
                .getConfigurationSection("dungeons");
        if (dungeons == null) return;

        for (String dungeonKey : dungeons.getKeys(false)) {
            ConfigurationSection dungeon = dungeons.getConfigurationSection(dungeonKey);
            if (dungeon == null) continue;

            ConfigurationSection miningAreas = dungeon.getConfigurationSection("miningAreas");
            if (miningAreas == null) continue;

            String worldName = dungeon.getString("world", "world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("[MiningZone] World '" + worldName + "' not found for dungeon: " + dungeonKey);
                continue;
            }

            for (String areaKey : miningAreas.getKeys(false)) {
                ConfigurationSection area = miningAreas.getConfigurationSection(areaKey);
                if (area == null) continue;

                int x1 = area.getInt("pos1.x"), y1 = area.getInt("pos1.y"), z1 = area.getInt("pos1.z");
                int x2 = area.getInt("pos2.x"), y2 = area.getInt("pos2.y"), z2 = area.getInt("pos2.z");
                int respawnSeconds = area.getInt("regeneration_seconds", 120);

                // Erze mit Weight + Vein-Size laden
                List<OreEntry> ores = new ArrayList<>();
                ConfigurationSection oresSection = area.getConfigurationSection("ores");
                if (oresSection != null) {
                    for (String oreKey : oresSection.getKeys(false)) {
                        ConfigurationSection ore = oresSection.getConfigurationSection(oreKey);
                        if (ore == null) continue;
                        try {
                            Material mat = Material.valueOf(ore.getString("material", ""));
                            int weight = ore.getInt("weight", 10);
                            int minVein = ore.getInt("min_vein", 1);
                            int maxVein = ore.getInt("max_vein", 1);
                            ores.add(new OreEntry(mat, weight, minVein, maxVein));
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("[MiningZone] Unknown material in " + areaKey + ": " + ore.getString("material"));
                        }
                    }
                }

                if (ores.isEmpty()) continue;

                zones.add(new MiningZone(world, x1, y1, z1, x2, y2, z2, respawnSeconds, ores));
                plugin.getLogger().info("[MiningZone] Loaded area '" + areaKey + "' for dungeon: " + dungeonKey);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Location loc = block.getLocation();

        MiningZone matchedZone = null;
        for (MiningZone zone : zones) {
            if (zone.contains(loc) && zone.isOre(block.getType())) {
                matchedZone = zone;
                break;
            }
        }
        if (matchedZone == null) return;

        e.setDropItems(false);

        Material originalOre = block.getType();
        String key = locationKey(loc);
        block.setType(Material.BEDROCK);
        pendingRespawn.put(key, originalOre);

        int ticks = matchedZone.getRespawnSeconds() * 20;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingRespawn.containsKey(key)) {
                block.setType(originalOre);
                pendingRespawn.remove(key);
            }
        }, ticks);
    }

    private void startRespawnTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (MiningZone zone : zones) {
                for (int x = zone.getMinX(); x <= zone.getMaxX(); x++) {
                    for (int y = zone.getMinY(); y <= zone.getMaxY(); y++) {
                        for (int z = zone.getMinZ(); z <= zone.getMaxZ(); z++) {
                            Location loc = new Location(zone.getWorld(), x, y, z);
                            String key = locationKey(loc);
                            Block block = loc.getBlock();
                            if (block.getType() == Material.BEDROCK && !pendingRespawn.containsKey(key)) {
                                block.setType(zone.getRandomOre());
                            }
                        }
                    }
                }
            }
        }, 20 * 60 * 10L, 20 * 60 * 10L);
    }

    private String locationKey(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    // ── Inner classes ─────────────────────────────────────────

    public record OreEntry(Material material, int weight, int minVein, int maxVein) {}

    public static class MiningZone {
        private final World world;
        private final int minX, minY, minZ, maxX, maxY, maxZ;
        private final int respawnSeconds;
        private final List<OreEntry> ores;
        private final int totalWeight;

        public MiningZone(World world, int x1, int y1, int z1, int x2, int y2, int z2,
                          int respawnSeconds, List<OreEntry> ores) {
            this.world = world;
            this.minX = Math.min(x1, x2); this.maxX = Math.max(x1, x2);
            this.minY = Math.min(y1, y2); this.maxY = Math.max(y1, y2);
            this.minZ = Math.min(z1, z2); this.maxZ = Math.max(z1, z2);
            this.respawnSeconds = respawnSeconds;
            this.ores = ores;
            this.totalWeight = ores.stream().mapToInt(OreEntry::weight).sum();
        }

        public boolean contains(Location loc) {
            if (!loc.getWorld().equals(world)) return false;
            int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
            return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
        }

        public boolean isOre(Material mat) {
            return ores.stream().anyMatch(o -> o.material() == mat);
        }

        // Weighted random ore auswählen
        public Material getRandomOre() {
            int roll = new Random().nextInt(totalWeight);
            int cumulative = 0;
            for (OreEntry ore : ores) {
                cumulative += ore.weight();
                if (roll < cumulative) return ore.material();
            }
            return ores.get(0).material();
        }

        public World getWorld() { return world; }
        public int getMinX() { return minX; }
        public int getMinY() { return minY; }
        public int getMinZ() { return minZ; }
        public int getMaxX() { return maxX; }
        public int getMaxY() { return maxY; }
        public int getMaxZ() { return maxZ; }
        public int getRespawnSeconds() { return respawnSeconds; }
        public List<OreEntry> getOres() { return ores; }
    }
}