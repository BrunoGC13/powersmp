package net.tgc.powersmp.dungeon;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DungeonLootManager {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String PREFIX = "<grey>[<gold>Dungeon<grey>] <reset><white>";

    private final Main plugin;
    private final Map<String, List<LootOrb>> orbs = new HashMap<>();

    public DungeonLootManager(Main plugin) {
        this.plugin = plugin;
    }

    // ── Called once on enable, and again every 30 s per dungeon in the task ──

    public void spawnOrbsForAllDungeons() {
        ConfigurationSection dungeons = plugin.getDungeonsData().getConfigurationSection("dungeons");
        if (dungeons == null) return;

        for (String key : dungeons.getKeys(false)) {
            ConfigurationSection dungeon = dungeons.getConfigurationSection(key);
            if (dungeon == null || !dungeon.getBoolean("active", true)) continue;
            if (dungeon.getConfigurationSection("loot") == null) continue;
            spawnOrbsForDungeon(key, dungeon);
        }
    }

    public void spawnOrbsForDungeon(String key, ConfigurationSection dungeon) {
        String worldName = dungeon.getString("world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        double cx = dungeon.getDouble("center.x");
        double cy = dungeon.getDouble("center.y");
        double cz = dungeon.getDouble("center.z");

        ConfigurationSection lootCfg = dungeon.getConfigurationSection("loot");
        if (lootCfg == null) return;

        double scatter = lootCfg.getDouble("scatter_radius",
                dungeon.getDouble("trigger_radius", 40) * 0.5);
        int max = lootCfg.getInt("max_entities", 6);

        List<LootOrb> list = orbs.computeIfAbsent(key, k -> new ArrayList<>());

        // Remove orbs whose ArmorStand has gone (chunk unloaded / server restarted)
        list.removeIf(orb -> {
            Entity e = Bukkit.getEntity(orb.getEntityId());
            return e == null || e.isDead();
        });

        int needed = max - list.size();
//        plugin.getLogger().info("[DungeonLoot] [" + key + "] orbs present=" + list.size()
//                + " max=" + max + " spawning=" + needed
//                + " center=[" + worldName + " " + cx + "," + cy + "," + cz + "] scatter=" + scatter);

        double minSep = Math.max(15, scatter / 4.0);

        for (int i = 0; i < needed; i++) {
            Location loc = findFloor(world, cx, cy, cz, scatter, list, minSep);
            if (loc == null) {
//                plugin.getLogger().warning("[DungeonLoot] [" + key + "] findFloor() returned null for orb " + (i + 1) + "/" + needed + " — no valid floor or separation constraint after " + (40 + (int)(scatter / 5)) + " attempts");
                continue;
            }

            ArmorStand stand = world.spawn(loc, ArmorStand.class, as -> {
                as.setVisible(false);
                as.setGravity(false);
                as.setMarker(true);
                as.setSmall(true);
                as.setInvulnerable(true);
                as.setPersistent(false);
                as.setCustomNameVisible(false);
            });

//            plugin.getLogger().info("[DungeonLoot] [" + key + "] Spawned orb #" + (list.size() + 1)
//                    + " at [" + (int) loc.getX() + "," + (int) loc.getY() + "," + (int) loc.getZ() + "]"
//                    + " uuid=" + stand.getUniqueId());
            list.add(new LootOrb(stand.getUniqueId(), loc.clone()));
        }
    }

    // ── Loot giving ───────────────────────────────────────────────────────────

    public void tryLoot(Player player, LootOrb orb, ConfigurationSection dungeon) {
        ConfigurationSection lootCfg = dungeon.getConfigurationSection("loot");
        if (lootCfg == null) return;

        long cooldownMs = lootCfg.getLong("cooldown_seconds", 600) * 1000L;
        if (!orb.canLoot(player.getUniqueId(), cooldownMs)) return;

        orb.markLooted(player.getUniqueId());

        ItemStack reward = rollLoot(lootCfg.getConfigurationSection("loot_table"));
        if (reward == null) return;

        player.getInventory().addItem(reward);

        String displayName = reward.hasItemMeta() && reward.getItemMeta().hasDisplayName()
                ? MM.serialize(reward.getItemMeta().displayName())
                : reward.getType().name().replace('_', ' ').toLowerCase();

        player.sendMessage(MM.deserialize(PREFIX + "You found <yellow>" + reward.getAmount() + "x <white>" + displayName));

        // Burst at orb on loot
        Location loc = orb.getLocation();
        loc.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc.clone().add(0, 0.5, 0),
                25, 0.3, 0.4, 0.3, 0.05);
    }

    // ── Cleanup (onDisable) ───────────────────────────────────────────────────

    public void removeAll() {
        for (List<LootOrb> list : orbs.values()) {
            for (LootOrb orb : list) {
                Entity e = Bukkit.getEntity(orb.getEntityId());
                if (e != null) e.remove();
            }
        }
        orbs.clear();
    }

    // ── Loot table rolling ────────────────────────────────────────────────────

    private ItemStack rollLoot(ConfigurationSection table) {
        if (table == null) return null;

        List<ConfigurationSection> entries = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        int total = 0;

        for (String key : table.getKeys(false)) {
            ConfigurationSection entry = table.getConfigurationSection(key);
            if (entry == null) continue;
            int w = entry.getInt("weight", 10);
            entries.add(entry);
            weights.add(w);
            total += w;
        }

        if (entries.isEmpty()) return null;

        int roll = ThreadLocalRandom.current().nextInt(total);
        int cumulative = 0;
        for (int i = 0; i < entries.size(); i++) {
            cumulative += weights.get(i);
            if (roll < cumulative) return buildReward(entries.get(i));
        }
        return buildReward(entries.get(0));
    }

    private ItemStack buildReward(ConfigurationSection entry) {
        int min    = entry.getInt("min", 1);
        int max    = entry.getInt("max", min);
        int amount = min >= max ? min : ThreadLocalRandom.current().nextInt(min, max + 1);

        String customKey = entry.getString("custom_item");
        if (customKey != null) {
            NamespacedKey key = NamespacedKey.fromString(customKey);
            if (key != null) {
                ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                if (stack != null) {
                    stack.setAmount(amount);
                    return stack;
                }
            }
        }

        String matName = entry.getString("material");
        if (matName == null) return null;
        Material mat = Material.matchMaterial(matName);
        if (mat == null) return null;
        return new ItemStack(mat, amount);
    }

    // ── Position finding ──────────────────────────────────────────────────────

    private Location findFloor(World world, double cx, double cy, double cz,
                               double scatter, List<LootOrb> existing, double minSep) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double minSepSq = minSep * minSep;

        // More attempts for a larger radius so we still fill the quota
        int maxAttempts = 40 + (int) (scatter / 5);

        outer:
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Uniform disk: sqrt keeps density even across the whole radius
            double angle = rng.nextDouble() * 2 * Math.PI;
            double dist  = Math.sqrt(rng.nextDouble()) * scatter;
            int x = (int) (cx + Math.cos(angle) * dist);
            int z = (int) (cz + Math.sin(angle) * dist);

            // Load the chunk if needed — dungeon area chunks exist on disk already
            if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                world.loadChunk(x >> 4, z >> 4, false);
                if (!world.isChunkLoaded(x >> 4, z >> 4)) continue; // chunk doesn't exist (void, etc.)
            }

            // Search downward from center Y for a solid floor with two air blocks above
            for (int dy = 0; dy <= 15; dy++) {
                int y = (int) cy - dy;
                if (y < world.getMinHeight()) break;
                if (!new Location(world, x, y, z).getBlock().getType().isSolid()) continue;
                if (!new Location(world, x, y + 1, z).getBlock().getType().isAir()) continue;
                if (!new Location(world, x, y + 2, z).getBlock().getType().isAir()) continue;

                Location candidate = new Location(world, x + 0.5, y + 1.0, z + 0.5);

                // Reject if too close to an already-placed orb
                for (LootOrb placed : existing) {
                    Location p = placed.getLocation();
                    double dx = candidate.getX() - p.getX();
                    double dz = candidate.getZ() - p.getZ();
                    if (dx * dx + dz * dz < minSepSq) continue outer;
                }

                return candidate;
            }
        }
        return null;
    }

    public Map<String, List<LootOrb>> getOrbs() {
        return orbs;
    }
}
