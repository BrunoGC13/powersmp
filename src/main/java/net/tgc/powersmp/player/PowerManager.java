package net.tgc.powersmp.player;

import org.bukkit.entity.Player;

/**
 * Convenience façade for power operations.
 * Can also be accessed via PowerSMPAPI.
 */
public final class PowerManager {
    private PowerManager() {}

    private static final PlayerDataManager data = PlayerDataManager.getInstance();

    public static double getPower(Player player) { return data.getPower(player); }
    public static double getMaxPower(Player player) { return data.getMaxPower(player); }
    public static void setPower(Player player, double power) { data.setPower(player, power); }
    public static void setMaxPower(Player player, double max) { data.setMaxPower(player, max); }
    public static void addPower(Player player, double amount) { data.addPower(player, amount); }
    public static void removePower(Player player, double amount) { data.removePower(player, amount); }
    public static boolean hasPower(Player player, double amount) { return data.hasPower(player, amount); }

    /**
     * Atomically checks and consumes power. Returns true if successful, false if not enough power.
     */
    public static boolean consumePower(Player player, double amount) {
        if (!hasPower(player, amount)) return false;
        removePower(player, amount);
        return true;
    }
}
