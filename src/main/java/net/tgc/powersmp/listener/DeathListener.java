package net.tgc.powersmp.listener;

import net.tgc.powersmp.player.HeartManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Victim loses 2 hearts
        HeartManager.removeHearts(victim, 2);

        // Killer (if a different player) gains 1 heart
        if (killer != null && !killer.equals(victim)) {
            HeartManager.addHearts(killer, 1);
        }
    }
}
