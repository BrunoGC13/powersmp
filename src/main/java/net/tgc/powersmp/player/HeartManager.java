package net.tgc.powersmp.player;

import org.bukkit.entity.Player;

/**
 * Convenience façade for heart operations.
 * Can also be accessed via PowerSMPAPI.
 */
public final class HeartManager {
    private HeartManager() {}

    private static final PlayerDataManager data = PlayerDataManager.getInstance();

    public static int getHearts(Player player) { return data.getHearts(player); }
    public static void setHearts(Player player, int hearts) { data.setHearts(player, hearts); }
    public static void addHearts(Player player, int amount) { data.addHearts(player, amount); }
    public static void removeHearts(Player player, int amount) { data.removeHearts(player, amount); }
}
