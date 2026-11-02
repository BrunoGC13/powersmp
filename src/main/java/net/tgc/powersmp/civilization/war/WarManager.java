package net.tgc.powersmp.civilization.war;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class WarManager {
    private Main plugin;

    public WarManager(Main plugin) {
        this.plugin = plugin;
    }

    public void startWar(War war) {
        if (plugin.getCiviliazationData().getBoolean("civilizations." + war.getTarget().getName() + ".in-war")) return;
        if (plugin.getCiviliazationData().getBoolean("civilizations." + war.getAttacker().getName() + ".in-war")) return;

        plugin.getWarsData().set("wars." + war.getId() + ".id", war.getId());
        plugin.getWarsData().set("wars." + war.getId() + ".attacker", war.getAttacker().getName());
        plugin.getWarsData().set("wars." + war.getId() + ".target", war.getTarget().getName());
        plugin.getWarsData().set("wars." + war.getId() + ".isWon", war.isWon());

        plugin.saveWarsData();

        plugin.getCiviliazationData().set("civilizations." + war.getAttacker().getName() + ".in-war", true);
        plugin.getCiviliazationData().set("civilizations." + war.getTarget().getName() + ".in-war", true);

        plugin.getCiviliazationData().set("civilizations." + war.getAttacker().getName() + ".in-war-with", war.getTarget().getName());
        plugin.getCiviliazationData().set("civilizations." + war.getTarget().getName() + ".in-war-with", war.getAttacker().getName());

        plugin.saveCivilizationsData();

        List<UUID> targetCivMembers = plugin.getCivilizationRegistry().getMembers(war.getTarget());
        for (UUID member : targetCivMembers) {
            Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage(MiniMessage.miniMessage().deserialize("<gray>[<yellow>Civilization<gray>] <red>The <orange>" + war.getAttacker().getName() + " <red>civilization has started a war with you!"));
        }

        List<UUID> attackerCivMembers = plugin.getCivilizationRegistry().getMembers(war.getTarget());
        for (UUID member : attackerCivMembers) {
            Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage(MiniMessage.miniMessage().deserialize("<gray>[<yellow>Civilization<gray>] <red>Your civilization started a war with the <orange>" + war.getTarget().getName() + " <red>civilization!"));
        }
    }
}

