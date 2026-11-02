package net.tgc.powersmp.listener;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.tgc.powersmp.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class MythicDungeonListener implements Listener {

    private final Main plugin;
    private final DungeonManager dungeonManager;

    public MythicDungeonListener(Main plugin, DungeonManager dungeonManager) {
        this.plugin = plugin;
        this.dungeonManager = dungeonManager;
    }

    @EventHandler
    public void onMythicDeath(MythicMobDeathEvent e) {
        String mobId = e.getMobType().getInternalName();

        ConfigurationSection dungeonsSection = plugin.getDungeonsData().getConfigurationSection("dungeons");
        if (dungeonsSection == null) return;

        for (String dungeonKey : dungeonsSection.getKeys(false)) {
            ConfigurationSection dungeon = dungeonsSection.getConfigurationSection(dungeonKey);
            if (dungeon == null) continue;

            String bossMob = dungeon.getString("boss_mob", "");
            if (!bossMob.equals(mobId)) continue;

            // Collect all players within trigger radius to stamp with cooldown
            String worldName = dungeon.getString("world", "world");
            World world = plugin.getServer().getWorld(worldName);
            double triggerRadius = dungeon.getDouble("trigger_radius", 80);

            List<Player> nearby = List.of();
            if (world != null) {
                double bossX = dungeon.getDouble("boss_spawn.x");
                double bossY = dungeon.getDouble("boss_spawn.y");
                double bossZ = dungeon.getDouble("boss_spawn.z");
                Location bossLoc = new Location(world, bossX, bossY, bossZ);
                double radiusSq = triggerRadius * triggerRadius;
                nearby = world.getPlayers().stream()
                        .filter(p -> p.getLocation().distanceSquared(bossLoc) <= radiusSq)
                        .toList();
            }

            dungeonManager.applyBossCooldown(dungeonKey, dungeon, nearby);
            break;
        }
    }
}
