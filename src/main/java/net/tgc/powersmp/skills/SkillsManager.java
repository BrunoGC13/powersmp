package net.tgc.powersmp.skills;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.civilization.Civilization;
import org.bukkit.entity.Player;

public class SkillsManager {
    private Main plugin;
    public SkillsManager(Main plugin) {
        this.plugin = plugin;
    }

    public void levelUp(Skill skill, Player player) {
        int currentLevel = plugin.getPlayerData().getInt("players." + player.getUniqueId() + ".skills." + skill.getName() + ".level");
        int newLevel = currentLevel + 1;

        plugin.getPlayerData().set("players." + player.getUniqueId() + ".skills." + skill.getName() + ".level", newLevel);
        plugin.getLevelManager().addXp(player, calcXp(newLevel));

        int currentTarget = plugin.getPlayerData().getInt("players." + player.getUniqueId() + ".skills." + skill.getName() + ".target");
        plugin.getPlayerData().set("players." + player.getUniqueId() + ".skills." + skill.getName() + ".target", calcNewTarget(currentTarget));
        plugin.getPlayerData().set("players." + player.getUniqueId() + ".skills." + skill.getName() + ".progress", 0);

        plugin.savePlayerData();

        if (!(plugin.getPlayerData().isSet("players." + player.getUniqueId() + ".civilization"))) return;
        Civilization civ = plugin.getCivilizationRegistry().getCivilization(plugin.getPlayerData().getString("players." + player.getUniqueId() + ".civilization"));
        plugin.getCivilizationLevelManager().addXP(civ, 1);
    }
    public int calcXp(int level) {
        return (int) (level * plugin.getConfig().getDouble("xp-rewards.skills-levelup", 1.5));
    }

    public int calcNewTarget(int level) {
        return (int) (level * plugin.getConfig().getDouble("target-multiplier"));
    }

    public void addProgress(int amount, Player player, Skill skill) {
        int currentProgress = plugin.getPlayerData().getInt("players." + player.getUniqueId() + ".skills." + skill.getName() + ".progress");
        int newProgress = currentProgress + amount;
        if (newProgress > plugin.getPlayerData().getInt("players." + player.getUniqueId() + ".skills." + skill.getName() + ".target")) {
            levelUp(skill, player);
            return;
        }
        plugin.getPlayerData().set("players." + player.getUniqueId() + ".skills." + skill.getName() + ".progress", newProgress);
        plugin.savePlayerData();
    }

    public int getLevel(Player player, Skill skill) {
        return plugin.getPlayerData().getInt("players." + player.getUniqueId() + ".skills." + skill.getName() + ".level");
    }

    public int getProgress(Player player, Skill skill) {
        return plugin.getPlayerData().getInt("players." + player.getUniqueId() + ".skills." + skill.getName() + ".progress");
    }

    public int getTarget(Player player, Skill skill) {
        return plugin.getPlayerData().getInt("players." + player.getUniqueId() + ".skills." + skill.getName() + ".target");
    }

    public void initPlayer(Player player, Skill skill) {
        String base = "players." + player.getUniqueId() + ".skills." + skill.getName() + ".";
        if (plugin.getPlayerData().contains(base + "level")) return;
        plugin.getPlayerData().set(base + "level", 1);
        plugin.getPlayerData().set(base + "progress", 0);
        plugin.getPlayerData().set(base + "target", skill.getTarget());
        plugin.savePlayerData();
    }
}
