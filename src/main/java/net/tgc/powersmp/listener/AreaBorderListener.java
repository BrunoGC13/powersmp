package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.dungeon.DungeonArea;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class AreaBorderListener implements Listener {

    private final Main plugin;

    public AreaBorderListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent e) {
        // Nur prüfen wenn Block gewechselt wurde — spart Performance
        if (!hasCrossedBlock(e)) return;

        Player player = e.getPlayer();
        String dungeonId = plugin.getDungeonAreaManager().getDungeonIdForPlayer(player);
        if (dungeonId == null) return;

        Location to = e.getTo();

        // Alle Areas dieses Dungeons durchgehen
        for (DungeonArea area : plugin.getDungeonAreaRegistry().getAllAreas(dungeonId)) {
            if (!isInBorderZone(to, area)) continue;

            // Spieler hat Zugang? -> durchlassen
            if (plugin.getDungeonAreaManager().hasAccess(player, dungeonId, area)) continue;

            // Kein Zugang -> zurückwerfen
            e.setCancelled(true);
            bounceBack(player, e.getFrom(), area);
            return;
        }
    }

    private void bounceBack(Player player, Location from, DungeonArea area) {
        // Sicher an die letzte erlaubte Position teleportieren
        Location safe = from.clone();
        safe.setYaw(player.getLocation().getYaw());
        safe.setPitch(player.getLocation().getPitch());
        player.teleport(safe);

        // Feedback
        player.sendMessage("§c✦ Dieser Bereich ist noch nicht zugänglich.");

        // Zeige welche Conditions fehlen
        if (area.getUnlockConditions() != null) {
            area.getUnlockConditions().stream()
                    .filter(c -> !c.isMet(player))
                    .forEach(c -> player.sendMessage("§7  ➔ " + c.getDescription()));
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        player.spawnParticle(Particle.BLOCK_CRUMBLE,
                player.getLocation().add(0, 1, 0),
                20, 0.3, 0.3, 0.3, 0.1,
                org.bukkit.Material.BARRIER.createBlockData()
        );
    }

    // Prüft ob der Spieler innerhalb der Border-Box der Area ist
    // border1/border2 definieren den "gesperrten" Eingangsbereich
    private boolean isInBorderZone(Location loc, DungeonArea area) {
        Location b1 = area.getBorder1();
        Location b2 = area.getBorder2();
        if (b1 == null || b2 == null) return false;
        if (!loc.getWorld().equals(b1.getWorld())) return false;

        double minX = Math.min(b1.getX(), b2.getX());
        double maxX = Math.max(b1.getX(), b2.getX());
        double minY = Math.min(b1.getY(), b2.getY());
        double maxY = Math.max(b1.getY(), b2.getY());
        double minZ = Math.min(b1.getZ(), b2.getZ());
        double maxZ = Math.max(b1.getZ(), b2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    // Nur wenn tatsächlich ein Block-Wechsel stattfand
    private boolean hasCrossedBlock(PlayerMoveEvent e) {
        if (e.getTo() == null) return false;
        return e.getFrom().getBlockX() != e.getTo().getBlockX()
                || e.getFrom().getBlockY() != e.getTo().getBlockY()
                || e.getFrom().getBlockZ() != e.getTo().getBlockZ();
    }
}