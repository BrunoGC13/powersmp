package net.tgc.powersmp.dungeon;

import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DungeonLootTask extends BukkitRunnable {

    private final Main plugin;
    private final DungeonLootManager manager;
    private int tick = 0;

    private DungeonLootTask(Main plugin, DungeonLootManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public static DungeonLootTask start(Main plugin, DungeonLootManager manager) {
        DungeonLootTask task = new DungeonLootTask(plugin, manager);
        task.runTaskTimer(plugin, 20L, 1L); // 1s startup delay, then every tick
        return task;
    }

    @Override
    public void run() {
        tick++;

        ConfigurationSection dungeons = plugin.getDungeonsData().getConfigurationSection("dungeons");
        if (dungeons == null) return;

        for (String dungeonKey : dungeons.getKeys(false)) {
            ConfigurationSection dungeon = dungeons.getConfigurationSection(dungeonKey);
            if (dungeon == null || !dungeon.getBoolean("active", true)) continue;

            ConfigurationSection lootCfg = dungeon.getConfigurationSection("loot");
            if (lootCfg == null) continue;

            List<LootOrb> orbList = manager.getOrbs().getOrDefault(dungeonKey, List.of());

            for (LootOrb orb : orbList) {
                Location loc = orb.getLocation();
                World world = loc.getWorld();
                if (world == null) continue;
                if (!world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) continue;

                // ── Particles (every 5 ticks) ────────────────────────────────
                if (tick % 5 == 0) {
                    Location center = loc.clone().add(0, 0.6, 0);
                    // Drifting white glow
                    world.spawnParticle(Particle.END_ROD, center, 3, 0.15, 0.2, 0.15, 0.01);
                    // Purple magic sparkle
                    world.spawnParticle(Particle.WITCH, center, 2, 0.1, 0.15, 0.1, 0.01);
                }

                // ── Proximity check (every 10 ticks = 0.5 s) ────────────────
                if (tick % 10 == 0) {
                    double radius = lootCfg.getDouble("loot_radius", 3.5);
                    double radiusSq = radius * radius;
                    for (Player player : world.getPlayers()) {
                        if (player.getLocation().distanceSquared(loc) <= radiusSq) {
                            manager.tryLoot(player, orb, dungeon);
                        }
                    }
                }
            }

            // ── Refill missing orbs every 30 s ──────────────────────────────
            if (tick % 600 == 0) {
                manager.spawnOrbsForDungeon(dungeonKey, dungeon);
            }
        }

        if (tick >= 1200) tick = 0; // reset every 60 s to avoid overflow drift
    }
}
