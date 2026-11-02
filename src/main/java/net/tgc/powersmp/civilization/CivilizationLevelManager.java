package net.tgc.powersmp.civilization;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class CivilizationLevelManager implements Listener {
    private Main plugin;
    public CivilizationLevelManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addXP(Civilization civ, int xp) {
        int currentXP = plugin.getCiviliazationData().getInt("civilizations." + civ.getName() + ".xp");
        int newXP = currentXP + xp;

        if (newXP > 50) {
            levelUp(civ);
            return;
        }

        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".xp", newXP);
        plugin.saveCivilizationsData();
    }

    public void levelUp(Civilization civ) {
        int currentLevel = plugin.getCiviliazationData().getInt("civilizations." + civ.getName() + ".level");
        int newLevel = currentLevel + 1;

        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".level", newLevel);
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".xp", 0);
        plugin.saveCivilizationsData();

        List<UUID> members = plugin.getCivilizationRegistry().getMembers(civ);
        for (UUID member : members) {
            int coins = 2500 * newLevel;
            OfflinePlayer p = Bukkit.getOfflinePlayer(member);
            plugin.getCoinsEconomy().depositPlayer(p, coins);
            Player onlinePlayer = Bukkit.getPlayer(member);
            onlinePlayer.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[<yellow>Civilization<gray>] <white>Your civilization has reached level <green>" + newLevel + " <white>and you gained <yellow>" + coins + " Coins!"));
        }

    }

}

