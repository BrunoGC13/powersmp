package net.tgc.powersmp.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PlayerEnterNetherListener implements Listener {

    private final Main plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public PlayerEnterNetherListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerTravel(PlayerPortalEvent e) {

        // Prevent possible NPEs from portal destination
        if (e.getTo() == null || e.getTo().getWorld() == null) {
            return;
        }

        String netherWorldName = plugin.getConfig().getString("worlds.nether");
        if (netherWorldName == null) {
            plugin.getLogger().warning("worlds.nether is missing from config.yml");
            return;
        }

        World nether = Bukkit.getWorld(netherWorldName);
        if (nether == null) {
            plugin.getLogger().warning("Nether world '" + netherWorldName + "' is not loaded.");
            return;
        }

        if (!e.getTo().getWorld().equals(nether)) {
            return;
        }

        Player p = e.getPlayer();
        String prefix = plugin.getConfig().getString("plugin-prefix", "");

        // Overall level requirement
        if (plugin.getConfig().getBoolean("nether.netherLevelRequirements")) {
            int level = plugin.getLevelManager().getLevel(p);
            int requiredLevel = plugin.getConfig().getInt("nether.requiredLevel");

            if (level < requiredLevel) {
                e.setCancelled(true);

                p.sendMessage(miniMessage.deserialize(
                        prefix +
                                "<red>You have to reach level <green>" +
                                requiredLevel +
                                " <red>before you can enter the nether!"
                ));
            }
        }

        // Skill requirement
        String requiredSkillId = plugin.getConfig().getString("nether.skill.requiredSkill");
        if (requiredSkillId == null) {
            plugin.getLogger().warning("nether.skill.requiredSkill is missing from config.yml");
            return;
        }

        Skill requiredSkill = plugin.getSkillsRegistry().getSkillData(requiredSkillId);

        if (requiredSkill == null) {
            plugin.getLogger().warning("Unknown skill: " + requiredSkillId);
            return;
        }

        if (plugin.getConfig().getBoolean("nether.skill.netherSkillRequirements")) {
            int level = plugin.getSkillsManager().getLevel(p, requiredSkill);
            int requiredLevel = plugin.getConfig().getInt("nether.skill.requiredSkillLevel");

            if (level < requiredLevel) {
                e.setCancelled(true);

                p.sendMessage(miniMessage.deserialize(
                        prefix +
                                "<red>You have to reach level <green>" +
                                requiredLevel +
                                " <red>in <green>" +
                                requiredSkill.getName() +
                                " <red>before you can enter the nether!"
                ));
            }
        }
    }
}