package net.tgc.powersmp.dungeon;

import io.lumine.mythic.bukkit.MythicBukkit;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Objects;

public class MobSpawnAreasManager {
    private Main plugin;
    public MobSpawnAreasManager(Main plugin) {
        this.plugin = plugin;
    }

    public void initMobSpawns() {
        ConfigurationSection dungeons = plugin.getDungeonsData().getConfigurationSection("dungeons");
        if (dungeons == null) return;

        for (String dungeonKey : dungeons.getKeys(false)) {
            ConfigurationSection dungeon = dungeons.getConfigurationSection(dungeonKey);
            ConfigurationSection mobSpawns = dungeon.getConfigurationSection("mobSpawns");
            if (mobSpawns == null) continue;

            World world = Bukkit.getWorld(Objects.requireNonNull(dungeon.getString("world")));
            if (world == null) continue;

            for (String spawnKey : mobSpawns.getKeys(false)) {
                ConfigurationSection spawn = mobSpawns.getConfigurationSection(spawnKey);
                if (spawn == null) continue;

                double x = spawn.getDouble("x");
                double y = spawn.getDouble("y");
                double z = spawn.getDouble("z");
                String mob = spawn.getString("mob", "RedstoneZombie");
                int amount = spawn.getInt("amount", 5);
                int intervalSeconds = spawn.getInt("interval_seconds", 30);
                Location loc = new Location(world, x, y, z);

                // Periodisch spawnen
                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    // Nur spawnen wenn weniger als amount Mobs in der Nähe
                    long nearbyCount = loc.getWorld().getNearbyEntities(loc, 20, 20, 20)
                            .stream()
                            .filter(e -> {
                                if (!(e instanceof org.bukkit.entity.LivingEntity)) return false;
                                try {
                                    var opt = MythicBukkit.inst().getMobManager().getActiveMob(e.getUniqueId());
                                    return opt.isPresent() && opt.get().getMobType().equals(mob);
                                } catch (Exception ignored) { return false; }
                            }).count();

                    int toSpawn = amount - (int) nearbyCount;
                    for (int i = 0; i < toSpawn; i++) {
                        MythicBukkit.inst().getMobManager().spawnMob(mob, loc);
                        // plugin.getLogger().info("Spawning Mob");
                    }
                }, 20L, intervalSeconds * 20L);
            }
        }
    }
}
