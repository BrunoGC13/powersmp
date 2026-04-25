package net.tgc.powersmp.listener;

import net.tgc.powersmp.player.PlayerDataManager;
import net.tgc.powersmp.player.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/** Regenerates player power at a steady rate (default: 2 per second). */
public class PowerRegenTask extends BukkitRunnable {
    @Override
    public void run() {
        double regen = PlayerDataManager.POWER_REGEN_PER_SECOND;
        for (Player player : Bukkit.getOnlinePlayers()) {
            double current = PowerManager.getPower(player);
            double max     = PowerManager.getMaxPower(player);
            if (current < max) {
                PowerManager.addPower(player, regen);
            }
        }
    }

    public static void start(Plugin plugin) {
        new PowerRegenTask().runTaskTimer(plugin, 20L, 20L);
    }
}
