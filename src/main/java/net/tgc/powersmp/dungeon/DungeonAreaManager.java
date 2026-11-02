package net.tgc.powersmp.dungeon;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.dungeon.unlock.UnlockCondition;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class DungeonAreaManager {
    private final Main plugin;

    public DungeonAreaManager(Main plugin) {
        this.plugin = plugin;
    }

    // Prüft ob der nächste Bereich unlockbar ist und führt Unlock durch
    public void checkUnlock(Player player, String dungeonId) {
        int currentStep = plugin.getDungeonPlayerData().getStep(player, dungeonId);
        DungeonArea nextArea = plugin.getDungeonAreaRegistry().getAreaByStep(dungeonId, currentStep + 1);

        if (nextArea == null) return; // kein nächster Bereich existiert
        if (!areaUnlockChecker(player, nextArea)) return; // Bedingungen nicht erfüllt

        unlockArea(player, dungeonId, nextArea);
    }

    // Gibt true zurück wenn alle Conditions der Area erfüllt sind
    public boolean areaUnlockChecker(Player player, DungeonArea area) {
        List<UnlockCondition> conditions = area.getUnlockConditions();
        if (conditions == null || conditions.isEmpty()) return true;
        return conditions.stream().allMatch(c -> c.isMet(player));
    }

    // Führt den tatsächlichen Unlock durch
    public void unlockArea(Player player, String dungeonId, DungeonArea area) {
        // Step erhöhen
        int newStep = area.getId();
        plugin.getDungeonPlayerData().setStep(player, dungeonId, newStep);

        // Spieler benachrichtigen
        player.sendMessage("");
        player.sendMessage("§e✦ §6Neuer Bereich freigeschaltet!");
        player.sendMessage("§7➔ §f" + area.getName());
        player.sendMessage("");

        // Sound
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

        // Hier kannst du später: Barrier entfernen, Partikel spawnen etc.
        removeBarrier(player, area);
    }

    // Prüft ob ein Spieler Zugang zu einer bestimmten Area hat
    public boolean hasAccess(Player player, String dungeonId, DungeonArea area) {
        int playerStep = plugin.getDungeonPlayerData().getStep(player, dungeonId);
        return playerStep >= area.getId();
    }

    // Placeholder — hier später Barrier-Blöcke entfernen / Packets senden
    private void removeBarrier(Player player, DungeonArea area) {
        // z.B. area.getBorder1() / getBorder2() nutzen um Barrier-Region zu füllen
        // mit Packets (per-Player) oder global mit Luft ersetzen
    }

    public String getDungeonIdForPlayer(Player player) {
        ConfigurationSection dungeons = plugin.getDungeonsData()
                .getConfigurationSection("dungeons");
        if (dungeons == null) return null;

        for (String dungeonKey : dungeons.getKeys(false)) {
            Dungeon dungeon = plugin.getDungeonAreaRegistry().loadDungeon(dungeonKey);
            if (dungeon == null) continue;
            if (isInsideDungeon(player.getLocation(), dungeon)) {
                return dungeonKey;
            }
        }
        return null;
    }

    private boolean isInsideDungeon(Location loc, Dungeon dungeon) {
        if (!loc.getWorld().equals(dungeon.getWorld())) return false;
        // Nur X/Z prüfen — Y ist im Dungeon sehr variabel
        double dx = loc.getX() - dungeon.getCenter().getX();
        double dz = loc.getZ() - dungeon.getCenter().getZ();
        return Math.sqrt(dx * dx + dz * dz) <= dungeon.getRadius();
    }
}