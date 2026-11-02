package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.dialog.RulesDialog;
import net.tgc.powersmp.player.PlayerDataManager;
import net.tgc.powersmp.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager.getInstance().initPlayer(event.getPlayer());

        plugin.getObjectiveManager().loadPlayer(event.getPlayer());
        plugin.getLevelManager().initPlayer(player);

        for (Skill skill : plugin.getSkillsRegistry().getAllSkills()) {
            plugin.getSkillsManager().initPlayer(player, skill);
        }

        if (!(plugin.getPlayerData().getBoolean("players." + player.getUniqueId() + ".acceptedRules", false)) && plugin.getConfig().getBoolean("enableRules")) {
            player.showDialog(new RulesDialog(plugin).getDialog());
        }
    }
}
