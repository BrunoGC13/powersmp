package net.tgc.powersmp.player;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class PlayerDataManager {
    private static PlayerDataManager instance;

    private NamespacedKey heartsKey;
    private NamespacedKey powerKey;
    private NamespacedKey maxPowerKey;
    private NamespacedKey powerBarKey;

    public static final int DEFAULT_HEARTS = 10;
    public static final double DEFAULT_MAX_POWER = 100.0;
    public static final double POWER_REGEN_PER_SECOND = 2.0;

    private PlayerDataManager() {}

    public static PlayerDataManager getInstance() {
        if (instance == null) instance = new PlayerDataManager();
        return instance;
    }

    public void init(Plugin plugin) {
        heartsKey   = new NamespacedKey(plugin, "hearts");
        powerKey    = new NamespacedKey(plugin, "power");
        maxPowerKey = new NamespacedKey(plugin, "max_power");
        powerBarKey = new NamespacedKey(plugin, "power_bar");
    }

    // -------------------------------------------------------------------------
    // Heart management
    // -------------------------------------------------------------------------

    public int getHearts(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(heartsKey, PersistentDataType.INTEGER, DEFAULT_HEARTS);
    }

    public void setHearts(Player player, int hearts) {
        int clamped = Math.max(1, hearts);
        player.getPersistentDataContainer().set(heartsKey, PersistentDataType.INTEGER, clamped);
        applyMaxHealth(player, clamped);
    }

    public void addHearts(Player player, int amount) {
        setHearts(player, getHearts(player) + amount);
    }

    public void removeHearts(Player player, int amount) {
        setHearts(player, getHearts(player) - amount);
    }

    private void applyMaxHealth(Player player, int hearts) {
        double maxHp = hearts * 2.0;
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(maxHp);
            if (player.getHealth() > maxHp) {
                player.setHealth(maxHp);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Power management
    // -------------------------------------------------------------------------

    public double getPower(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(powerKey, PersistentDataType.DOUBLE, DEFAULT_MAX_POWER);
    }

    public double getMaxPower(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(maxPowerKey, PersistentDataType.DOUBLE, DEFAULT_MAX_POWER);
    }

    public void setPower(Player player, double power) {
        double clamped = Math.max(0, Math.min(power, getMaxPower(player)));
        player.getPersistentDataContainer().set(powerKey, PersistentDataType.DOUBLE, clamped);
    }

    public void addPower(Player player, double amount) {
        setPower(player, getPower(player) + amount);
    }

    public void removePower(Player player, double amount) {
        setPower(player, getPower(player) - amount);
    }

    public boolean hasPower(Player player, double amount) {
        return getPower(player) >= amount;
    }

    public void setMaxPower(Player player, double maxPower) {
        double clamped = Math.max(1, maxPower);
        player.getPersistentDataContainer().set(maxPowerKey, PersistentDataType.DOUBLE, clamped);
        if (getPower(player) > clamped) setPower(player, clamped);
    }

    // -------------------------------------------------------------------------
    // Power bar toggle
    // -------------------------------------------------------------------------

    public boolean isPowerBarEnabled(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(powerBarKey, PersistentDataType.BYTE, (byte) 0) == 1;
    }

    public void setPowerBarEnabled(Player player, boolean enabled) {
        player.getPersistentDataContainer()
                .set(powerBarKey, PersistentDataType.BYTE, enabled ? (byte) 1 : (byte) 0);
    }

    // -------------------------------------------------------------------------
    // Init on join
    // -------------------------------------------------------------------------

    public void initPlayer(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.has(heartsKey, PersistentDataType.INTEGER)) {
            pdc.set(heartsKey, PersistentDataType.INTEGER, DEFAULT_HEARTS);
        }
        if (!pdc.has(powerKey, PersistentDataType.DOUBLE)) {
            pdc.set(powerKey, PersistentDataType.DOUBLE, DEFAULT_MAX_POWER);
        }
        if (!pdc.has(maxPowerKey, PersistentDataType.DOUBLE)) {
            pdc.set(maxPowerKey, PersistentDataType.DOUBLE, DEFAULT_MAX_POWER);
        }
        applyMaxHealth(player, getHearts(player));
    }
}
