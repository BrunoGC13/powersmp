package net.tgc.powersmp.levelSystem;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.civilization.Civilization;
import net.tgc.powersmp.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class LevelManager {
    private final Main plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final String prefix;

    // Basis-Coins bei Level 1→2
    private static final double BASE_COINS = 75.0;
    // Multiplikator pro Level
    private static final double COINS_MULTIPLIER = 1.25;

    public LevelManager(Main plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    public void levelUp(Player player) {
        String uuid = player.getUniqueId().toString();
        int currentLevel = getLevel(player);
        int newLevel = currentLevel + 1;

        plugin.getPlayerData().set("players." + uuid + ".level", newLevel);
        plugin.getPlayerData().set("players." + uuid + ".xp", 0);

        // Coins skalieren: BASE * MULTIPLIER^(newLevel-1)
        double coins = BASE_COINS * Math.pow(COINS_MULTIPLIER, newLevel - 1);
        coins = Math.round(coins * 100.0) / 100.0;

        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        plugin.getCoinsEconomy().depositPlayer(op, coins);

        // Level Perks anwenden
        applyLevelPerks(player, newLevel);

        plugin.savePlayerData();

        // Nachricht + Sound
        player.sendMessage(mm.deserialize(prefix
                + "<green>Level Up! <white>You are now <green>Level " + newLevel
                + "<white>! (<gold>+" + String.format("%.2f", coins) + " Coins<white>)"));
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

        // Perk-Nachricht falls vorhanden
        String perkMsg = getPerkMessage(newLevel);
        if (perkMsg != null) {
            player.sendMessage(mm.deserialize(prefix + perkMsg));
        }

        if (!(plugin.getPlayerData().isSet("players." + player.getUniqueId() + ".civilization"))) return;
        Civilization civ = plugin.getCivilizationRegistry().getCivilization(plugin.getPlayerData().getString("players." + player.getUniqueId() + ".civilization"));
        plugin.getCivilizationLevelManager().addXP(civ, 1);
    }

    private void applyLevelPerks(Player player, int level) {
        PlayerDataManager pdm = PlayerDataManager.getInstance();

        // +1 Herz alle 5 Level ab Level 5
        if (level >= 5 && level % 5 == 0) {
            int currentHearts = pdm.getHearts(player);
            int newHearts = currentHearts + 1;
            pdm.setHearts(player, newHearts);

            // Max HP erhöhen
            var attr = player.getAttribute(Attribute.MAX_HEALTH);
            if (attr != null) attr.setBaseValue(newHearts * 2.0);
            player.setHealth(Math.min(player.getHealth() + 2, newHearts * 2.0));
        }

        // +10% Max Power alle 10 Level
        if (level % 10 == 0) {
            double currentMax = pdm.getMaxPower(player);
            pdm.setMaxPower(player, currentMax * 1.1);
        }

        // Level 10: Schnellerer Power-Regen (als Flag speichern)
        if (level == 10) {
            plugin.getPlayerData().set(
                    "players." + player.getUniqueId() + ".perks.fast_regen", true);
        }

        // Level 20: Zweiter Gem-Slot
        if (level == 20) {
            plugin.getPlayerData().set(
                    "players." + player.getUniqueId() + ".perks.extra_gem_slot", true);
        }

        // Level 30: +20% XP aus Quests
        if (level == 30) {
            plugin.getPlayerData().set(
                    "players." + player.getUniqueId() + ".perks.xp_boost", true);
        }
    }

    // In PlayerDataManager.initPlayer() oder direkt im PlayerJoinListener:
    public void initPlayer(Player player) {
        String base = "players." + player.getUniqueId() + ".";

        // Level init
        if (!plugin.getPlayerData().contains(base + "level")) {
            plugin.getPlayerData().set(base + "level", 1);
        }
        if (!plugin.getPlayerData().contains(base + "xp")) {
            plugin.getPlayerData().set(base + "xp", 0);
        }

        plugin.savePlayerData();
    }

    private String getPerkMessage(int level) {
        return switch (level) {
            case 5, 15, 25, 35, 45 -> "<red>Perk: <white>+1 Heart unlocked!";
            case 10 -> "<red>Perk: <white>+1 Heart! <blue>+ Power regenerates 50% faster!";
            case 20 -> "<red>Perk: <white>+1 Heart! <light_purple>+ Extra Gem Slot unlocked!";
            case 30 -> "<red>Perk: <white>+1 Heart! <green>+ +20% XP from all Quests!";
            default -> level >= 5 && level % 5 == 0 ? "<red>Perk: <white>+1 Heart unlocked!" : null;
        };
    }

    public void addXp(Player player, int xp) {
        String uuid = player.getUniqueId().toString();

        // XP-Boost Perk prüfen
        boolean xpBoost = plugin.getPlayerData().getBoolean(
                "players." + uuid + ".perks.xp_boost", false);
        if (xpBoost) xp = (int) (xp * 1.2);

        int currentXp = getXp(player);
        int newXp = currentXp + xp;

        if (newXp >= 100) {
            levelUp(player);
            int overflow = newXp - 100;
            if (overflow > 0) addXp(player, overflow);
            return;
        }

        plugin.getPlayerData().set("players." + uuid + ".xp", newXp);

        // 5 Coins pro XP-Gain
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        plugin.getCoinsEconomy().depositPlayer(op, 5);

        plugin.savePlayerData();
        player.sendMessage(mm.deserialize(prefix + "You gained <green>" + xp + " XP<white>!"));
    }

    public int getXp(Player player) {
        return plugin.getPlayerData().getInt("players." + player.getUniqueId() + ".xp");
    }

    public int getLevel(Player player) {
        return plugin.getPlayerData().getInt("players." + player.getUniqueId() + ".level");
    }

    // Coins die beim nächsten Level-Up warten
    public double getNextLevelCoins(Player player) {
        int nextLevel = getLevel(player) + 1;
        double coins = BASE_COINS * Math.pow(COINS_MULTIPLIER, nextLevel - 1);
        return Math.round(coins * 100.0) / 100.0;
    }

    public String getNextPerk(Player player) {
        int nextLevel = getLevel(player) + 1;

        return switch (nextLevel) {
            case 5, 15, 25, 35, 45 -> "❤ +1 Heart";
            case 10 -> "❤ +1 Heart + ⚡ Faster Power Regen";
            case 20 -> "❤ +1 Heart + ✦ Extra Gem Slot";
            case 30 -> "❤ +1 Heart + ⭐ +20% Quest XP";
            default -> {
                // Nächsten relevanten Perk suchen
                for (int i = nextLevel + 1; i <= nextLevel + 20; i++) {
                    if (i == 10) yield "❤ +1 Heart + ⚡ Faster Power Regen at Level 10";
                    if (i == 20) yield "❤ +1 Heart + ✦ Extra Gem Slot at Level 20";
                    if (i == 30) yield "❤ +1 Heart + ⭐ +20% Quest XP at Level 30";
                    if (i >= 5 && i % 5 == 0) yield "❤ +1 Heart at Level " + i;
                    if (i % 10 == 0) yield "⚡ +10% Max Power at Level " + i;
                }
                yield "None upcoming";
            }
        };
    }
}