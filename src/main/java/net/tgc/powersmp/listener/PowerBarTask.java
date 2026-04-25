package net.tgc.powersmp.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.tgc.powersmp.player.PlayerDataManager;
import net.tgc.powersmp.player.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PowerBarTask extends BukkitRunnable {
    private final PlayerDataManager data = PlayerDataManager.getInstance();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!data.isPowerBarEnabled(player)) continue;
            double power = PowerManager.getPower(player);
            double max   = PowerManager.getMaxPower(player);
            int bars = (int) Math.round((power / max) * 10);
            String filled   = "█".repeat(bars);
            String unfilled = "░".repeat(10 - bars);
            Component msg = Component.text("⚡ ", NamedTextColor.YELLOW)
                    .append(Component.text(filled, NamedTextColor.GOLD))
                    .append(Component.text(unfilled, NamedTextColor.DARK_GRAY))
                    .append(Component.text(String.format(" %.0f/%.0f", power, max), NamedTextColor.YELLOW));
            player.sendActionBar(msg);
        }
    }

    public static void start(Plugin plugin) {
        new PowerBarTask().runTaskTimer(plugin, 0L, 20L);
    }
}
