package net.tgc.powersmp.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class MobSpawnListener implements Listener {

    private final Main plugin;
    private final Random random = new Random();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public MobSpawnListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {

        LivingEntity entity = e.getEntity();
        Biome biome = entity.getLocation().getBlock().getBiome();

        // =========================
        // 🌍 GLOBAL HEALTH DISPLAY
        // =========================
        double minHealth = plugin.getConfig().getDouble("entities.minHealth");
        double maxHealth = plugin.getConfig().getDouble("entities.maxHealth");
        double randomHealth = minHealth + (maxHealth - minHealth) * random.nextDouble();

        if (!(entity instanceof Player)
                && !(entity instanceof Silverfish)
                && !(entity instanceof Villager)
                && !(entity instanceof ArmorStand)
                && !(entity instanceof TropicalFish)
                && !(entity instanceof Axolotl)
                && !(entity instanceof Salmon)
                && !(entity instanceof Cod)
                && !(entity instanceof Nautilus)
                && !(entity instanceof Allay)
                && !(entity instanceof AreaEffectCloud)
                && plugin.getConfig().getBoolean("mobHealthOverride")) {

            var maxHealthAttribute = entity.getAttribute(Attribute.MAX_HEALTH);

            if (maxHealthAttribute != null) {
                maxHealthAttribute.setBaseValue(randomHealth);
                entity.setHealth(randomHealth);
            }

            Bukkit.getScheduler().runTask(plugin, () -> updateCustomName(entity));

        }

        if (!plugin.getConfig().getBoolean("mobSpawnOverride")) {
            return;
        }

        ConfigurationSection mobs = plugin.getConfig().getConfigurationSection("mobs");
        if (mobs == null) {
            return;
        }

        // =========================
        // ⚡ CREEPER
        // =========================
        if (entity instanceof Creeper creeper) {

            ConfigurationSection cfg = mobs.getConfigurationSection("creeper");

            if (cfg != null) {
                double chance = cfg.getDouble("chargedChance");

                if (random.nextDouble() < chance) {
                    creeper.setPowered(true);
                }
            }
        }

        // =========================
        // 🧠 SKELETON
        // =========================
        if (entity instanceof Skeleton skeleton) {

            ConfigurationSection cfg = mobs.getConfigurationSection("skeleton");

            if (cfg != null) {

                double chance = cfg.getDouble("badlandsChance");

                if (biome == Biome.BADLANDS && random.nextDouble() < chance) {

                    int minPower = cfg.getInt("powerMin");
                    int maxPower = cfg.getInt("powerMax");

                    int power = minPower + random.nextInt(maxPower - minPower + 1);

                    ItemStack bow = new ItemStack(Material.BOW);
                    bow.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
                    bow.addUnsafeEnchantment(Enchantment.POWER, power);

                    Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(bow);
                }
            }
        }

        // =========================
        // 🧟 ZOMBIE
        // =========================
        if (entity instanceof Zombie zombie) {

            ConfigurationSection cfg = mobs.getConfigurationSection("zombie");

            if (cfg != null) {

                double chance = cfg.getDouble("gearChance");

                if (random.nextDouble() < chance) {

                    int minPower = cfg.getInt("powerMin");
                    int maxPower = cfg.getInt("powerMax");

                    int power = minPower + random.nextInt(maxPower - minPower + 1);

                    ItemStack sword = new ItemStack(Material.IRON_SWORD);
                    sword.addUnsafeEnchantment(Enchantment.SHARPNESS, power);
                    sword.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);

                    Objects.requireNonNull(zombie.getEquipment()).setItemInMainHand(sword);
                }

                Objects.requireNonNull(zombie.getEquipment())
                        .setHelmet(new ItemStack(Material.IRON_HELMET));
            }
        }

        // =========================
        // 🕷 SPIDER
        // =========================
        if (entity instanceof Spider spider) {
            spider.setAware(true);
        }
    }

    @EventHandler
    public void mobDamage(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof LivingEntity living)) {
            return;
        }

        if (living instanceof Player) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> updateCustomName(living));
    }

    @EventHandler
    public void mobHeal(EntityRegainHealthEvent e) {

        if (!(e.getEntity() instanceof LivingEntity living)) {
            return;
        }

        if (living instanceof Player) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> updateCustomName(living));
    }

    private void updateCustomName(LivingEntity entity) {

        if (!(plugin.getConfig().getBoolean("entities.showNameTag"))) return;

        if (!entity.isValid()) {
            return;
        }

        Attribute attribute = Attribute.MAX_HEALTH;

        if (entity.getAttribute(attribute) == null) {
            return;
        }

        int currentHealth = (int) Math.ceil(entity.getHealth());
        int maxHealth = (int) Math.ceil(
                Objects.requireNonNull(entity.getAttribute(attribute)).getValue()
        );

        String mobName = entity.getType().name()
                .toLowerCase()
                .replace("_", " ");

        entity.setCustomNameVisible(true);

        entity.customName(
                miniMessage.deserialize(
                        "<red><bold>" +
                                mobName +
                                " <reset><dark_gray>| <gray>" +
                                currentHealth +
                                "<dark_gray>/<gray>" +
                                maxHealth +
                                " <red>❤"
                )
        );
    }
}