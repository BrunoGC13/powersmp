package net.tgc.powersmp.objectives;

import net.tgc.powersmp.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class NpcAssignmentListener implements Listener {

    private final Main plugin;

    public NpcAssignmentListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        String mobName = resolveMobName(event.getEntity());
        plugin.getObjectiveManager().onMobKill(killer, mobName);
    }

    private String resolveMobName(Entity entity) {
        if (plugin.getMythicMobs() != null) {
            try {
                var opt = io.lumine.mythic.bukkit.MythicBukkit.inst()
                        .getMobManager().getActiveMob(entity.getUniqueId());
                if (opt.isPresent()) return opt.get().getMobType();
            } catch (Exception | NoClassDefFoundError ignored) {}
        }
        return entity.getType().name();
    }
}
