package net.tgc.powersmp.dungeon;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.tgc.powersmp.Main;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class LorePagesManager {

    private final Main plugin;
    private final NamespacedKey loreKey;
    private final NamespacedKey loreIdKey;

    // Gespeicherte Entities zum Cleanup
    private final List<Item> spawnedItems = new ArrayList<>();
    private final List<ArmorStand> spawnedStands = new ArrayList<>();
    private BukkitRunnable particleTask;

    public LorePagesManager(Main plugin) {
        this.plugin = plugin;
        this.loreKey = new NamespacedKey(plugin, "lore_page");
        this.loreIdKey = new NamespacedKey(plugin, "lore_page_id");
    }

    public void spawn() {
        ConfigurationSection dungeons = plugin.getDungeonsData().getConfigurationSection("dungeons");
        if (dungeons == null) return;

        for (String dungeonKey : dungeons.getKeys(false)) {
            ConfigurationSection lorePages = plugin.getDungeonsData()
                    .getConfigurationSection("dungeons." + dungeonKey + ".lorePages");
            if (lorePages == null) continue;

            for (String pageKey : lorePages.getKeys(false)) {
                ConfigurationSection page = lorePages.getConfigurationSection(pageKey);
                if (page == null) continue;
                spawnLorePage(page, pageKey);
            }
        }

        startParticleLoop();
    }

    private void spawnLorePage(ConfigurationSection page, String pageId) {
        World world = Bukkit.getWorld("world"); // ggf. aus Config lesen
        if (world == null) return;

        double x = page.getDouble("x");
        double y = page.getDouble("y");
        double z = page.getDouble("z");
        String key1 = page.getString("key1");
        String key2 = page.getString("key2");
        String name = page.getString("name", "Unknown Journal");
        List<String> lore = page.getStringList("lore");

        Location loc = new Location(world, x, y + 1, z); // +1 Y wie gewünscht

        // ItemStack bauen (Paper mit CustomModelData)
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e✦ §6" + name);
        meta.setLore(lore.stream().map(l -> "§7" + l).collect(java.util.stream.Collectors.toList()));
        meta.getPersistentDataContainer().set(loreKey, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(loreIdKey, PersistentDataType.STRING, pageId);
        item.setItemMeta(meta);
        item.setData(DataComponentTypes.ITEM_MODEL, Key.key(key1, key2));

        // Unsichtbarer ArmorStand als Halter damit das Item fixiert bleibt
        ArmorStand stand = world.spawn(loc, ArmorStand.class, as -> {
            as.setInvisible(true);
            as.setGravity(false);
            as.setCanPickupItems(false);
            as.setSmall(true);
            as.setPersistent(true);
            as.setCollidable(false);
            as.getEquipment().setHelmet(item); // Item auf dem Kopf = schwebend
        });

        // Item Entity zusätzlich für den visuellen Float-Effekt
        Item dropped = world.dropItem(loc, item);
        dropped.setPickupDelay(Integer.MAX_VALUE); // nicht aufhebbar
        dropped.setGravity(false);
        dropped.setVelocity(new Vector(0, 0, 0));
        dropped.setPersistent(true);
        dropped.setUnlimitedLifetime(true);
        dropped.getItemStack().getItemMeta(); // Sicherheit

        spawnedItems.add(dropped);
        spawnedStands.add(stand);
    }

    // Bobbing-Animation + Partikel für alle Lore Pages
    private void startParticleLoop() {
        particleTask = new BukkitRunnable() {
            double tick = 0;

            @Override
            public void run() {
                tick += 0.08;

                for (Item item : spawnedItems) {
                    if (!item.isValid()) continue;

                    Location base = item.getLocation().clone();

                    // Sanftes Auf-und-Ab (Bobbing)
                    double yOffset = Math.sin(tick) * 0.15;
                    item.teleport(base.clone().add(0, yOffset, 0));

                    // Partikel Kreis um das Item
                    spawnOrbitParticles(base);
                }
            }
        };
        particleTask.runTaskTimer(plugin, 0L, 2L);
    }

    private void spawnOrbitParticles(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        // Enchant-Glitter oben
        world.spawnParticle(
                Particle.ENCHANT,
                center.clone().add(0, 0.5, 0),
                3, 0.3, 0.3, 0.3, 0.05
        );

        // Goldener Schimmer
        world.spawnParticle(
                Particle.END_ROD,
                center.clone().add(0, 0.3, 0),
                1, 0.2, 0.2, 0.2, 0.01
        );
    }

    // Pickup-Event: in deinem DungeonListener aufrufen
    public boolean isLorePage(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(loreKey, PersistentDataType.BYTE);
    }

    public String getLorePageId(ItemStack item) {
        if (!isLorePage(item)) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(loreIdKey, PersistentDataType.STRING);
    }

    // Cleanup beim Plugin-Disable
    public void despawnAll() {
        if (particleTask != null) particleTask.cancel();
        spawnedItems.forEach(i -> { if (i.isValid()) i.remove(); });
        spawnedStands.forEach(s -> { if (s.isValid()) s.remove(); });
        spawnedItems.clear();
        spawnedStands.clear();
    }
}