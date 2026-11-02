package net.tgc.powersmp.listener;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobDeath implements Listener {
    public Main plugin;
    public MythicMobDeath(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void mythicMobDeath(MythicMobDeathEvent e) {
        Skill skill = plugin.getSkillsRegistry().getSkillData("hunting_skill");
        if (!((e.getKiller()) instanceof Player)) return;

        Player p = (Player) e.getKiller();
        plugin.getSkillsManager().addProgress(1, p, skill);
    }
}
