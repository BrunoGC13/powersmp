package net.tgc.powersmp;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.tgc.powersmp.levelSystem.LevelManager;
import net.tgc.powersmp.player.PlayerDataManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {
    private final Main plugin;

    public Placeholders(Main plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "powersmp";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "BrunoGC13";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "2.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            PlayerDataManager pdm = PlayerDataManager.getInstance();
            int    hearts   = pdm.getHearts(player);
            double power    = pdm.getPower(player);
            double netWorth = plugin.getPlayerData().getDouble("players." + player.getUniqueId() + ".networth");
            int    deaths   = player.getStatistic(Statistic.DEATHS);
            int    pvpKills = player.getStatistic(Statistic.PLAYER_KILLS);

            if (params.equalsIgnoreCase("level")) {
                return Integer.toString(plugin.getLevelManager().getLevel(player));
            }

            if (params.equalsIgnoreCase("xp")) {
                return Integer.toString(plugin.getLevelManager().getXp(player));
            }

            if (params.equalsIgnoreCase("hearts")) {
                return Integer.toString(hearts);
            }
            if (params.equalsIgnoreCase("kills")) {
                return Integer.toString(pvpKills);
            }
            if (params.equalsIgnoreCase("deaths")) {
                return Integer.toString(deaths);
            }
            if (params.equalsIgnoreCase("power")) {
                return Double.toString(power);
            }
            if (params.equalsIgnoreCase("objective")) {
                return plugin.getObjectiveManager()
                        .getTopObjective(player)
                        .map(o -> o.getIcon() + " " + o.getDisplayName())
                        .orElse("<gray>No active objective");
            }

            if (params.equalsIgnoreCase("net_worth")) {
                return Double.toString(netWorth);
            }
        }
        return null;
    }
}
