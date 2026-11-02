package net.tgc.powersmp.listener;

import io.lumine.mythic.bukkit.MythicBukkit;
import net.tgc.powersmp.Main;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class DungeonManager implements Listener {
    private final Main plugin;
    final Set<String> activeBosses = new HashSet<>();

    /**
     * Checks if a player is within a 2D area defined by two opposite corner locations.
     *
     * @param player The player to check.
     * @param loc1   The first corner of the region.
     * @param loc2   The diagonally opposite corner of the region.
     * @return true if the player is within the 2D boundaries and in the same world.
     */
    public static boolean isPlayerIn2DArea(Player player, Location loc1, Location loc2) {
        Location playerLoc = player.getLocation();

        // 1. Ensure the player is in the same world as the region points
        if (!playerLoc.getWorld().equals(loc1.getWorld()) || !playerLoc.getWorld().equals(loc2.getWorld())) {
            return false;
        }

        // 2. Calculate the true minimum and maximum boundaries for X
        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());

        // 3. Calculate the true minimum and maximum boundaries for Z
        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        // 4. Get player coordinates
        double playerX = playerLoc.getX();
        double playerZ = playerLoc.getZ();

        // 5. Evaluate if the player is inside the bounds (inclusive)
        return (playerX >= minX && playerX <= maxX) && (playerZ >= minZ && playerZ <= maxZ);
    }

    // dungeonKey → (playerUUID → cooldown-expiry timestamp)
    private final Map<String, Map<UUID, Long>> bossCooldowns = new HashMap<>();

    private static final long DEFAULT_BOSS_COOLDOWN_MS = 10 * 60 * 1000L; // 10 min

    public DungeonManager(Main plugin) {
        this.plugin = plugin;
    }

    public Set<String> getActiveBosses() {
        return activeBosses;
    }

    /** Called by MythicDungeonListener when the boss dies. Stamps every player in range. */
    public void applyBossCooldown(String dungeonKey, ConfigurationSection dungeon, Collection<? extends Player> nearbyPlayers) {
        long cooldownMs = dungeon.getLong("boss_cooldown_seconds", DEFAULT_BOSS_COOLDOWN_MS / 1000L) * 1000L;
        long expiry = System.currentTimeMillis() + cooldownMs;

        Map<UUID, Long> map = bossCooldowns.computeIfAbsent(dungeonKey, k -> new HashMap<>());
        for (Player p : nearbyPlayers) {
            map.put(p.getUniqueId(), expiry);
            p.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                    "<grey>[<gold>Dungeon<grey>] <reset><white>The boss has been defeated! You can challenge this dungeon again in <yellow>"
                    + (cooldownMs / 60_000L) + " minutes<white>."));
        }
        activeBosses.remove(dungeonKey);
        plugin.getLogger().info("[Dungeon] [" + dungeonKey + "] Boss died. Cooldown applied to "
                + nearbyPlayers.size() + " player(s) for " + (cooldownMs / 1000L) + "s.");
    }

    private boolean isOnCooldown(String dungeonKey, UUID playerId) {
        Map<UUID, Long> map = bossCooldowns.get(dungeonKey);
        if (map == null) return false;
        Long expiry = map.get(playerId);
        if (expiry == null) return false;
        if (System.currentTimeMillis() >= expiry) {
            map.remove(playerId); // expired — clean up
            return false;
        }
        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!e.hasChangedBlock()) return;

        Player player = e.getPlayer();
        ConfigurationSection dungeonsSection = plugin.getDungeonsData().getConfigurationSection("dungeons");
        if (dungeonsSection == null) return;

        boolean debug = plugin.isDebugMode();

        for (String treeKey : dungeonsSection.getKeys(false)) {
            ConfigurationSection dungeon = dungeonsSection.getConfigurationSection(treeKey);
            if (dungeon == null) continue;

            String worldName = dungeon.getString("world", "world");
            double bossX = dungeon.getDouble("boss_spawn.x");
            double bossY = dungeon.getDouble("boss_spawn.y");
            double bossZ = dungeon.getDouble("boss_spawn.z");
            double triggerRadius = dungeon.getDouble("trigger_radius");
            String bossMob = dungeon.getString("boss_mob");

            org.bukkit.World world = plugin.getServer().getWorld(worldName);
            if (world == null) {
                if (debug) plugin.getLogger().warning("[DungeonDebug] [" + treeKey + "] world '" + worldName + "' is NULL — skipping");
                continue;
            }

            if (!player.getWorld().equals(world)) return;

            Location bossLoc = new Location(world, bossX, bossY, bossZ);
            double distance = player.getLocation().distance(bossLoc);
            boolean onCooldown = isOnCooldown(treeKey, player.getUniqueId());

            if (debug) {
                plugin.getLogger().info("[DungeonDebug] [" + treeKey + "] player=" + player.getName()
                        + " dist=" + String.format("%.1f", distance)
                        + " trigger=" + triggerRadius
                        + " bossActive=" + activeBosses.contains(treeKey)
                        + " onCooldown=" + onCooldown
                        + " mmHooked=" + (plugin.getMythicMobs() != null)
                        + " bossMob=" + bossMob
                        + " bossLoc=[" + worldName + " " + bossX + "," + bossY + "," + bossZ + "]");
            }

            if (distance > triggerRadius) continue;
            if (activeBosses.contains(treeKey)) continue;
            if (onCooldown) continue;

            if (plugin.getMythicMobs() == null) {
                plugin.getLogger().warning("[Dungeon] [" + treeKey + "] Boss trigger by " + player.getName()
                        + " but MythicMobs hook is null — boss not spawned!");
                continue;
            }

            if (bossMob == null || bossMob.isBlank()) {
                plugin.getLogger().warning("[Dungeon] [" + treeKey + "] boss_mob is null/blank — boss not spawned!");
                continue;
            }

            activeBosses.add(treeKey);
            plugin.getLogger().info("[Dungeon] [" + treeKey + "] Spawning boss '" + bossMob
                    + "' at [" + worldName + " " + bossX + "," + bossY + "," + bossZ + "]"
                    + " triggered by " + player.getName());
            MythicBukkit.inst().getMobManager().spawnMob(bossMob, bossLoc);
        }
    }
}
