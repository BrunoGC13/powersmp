package net.tgc.powersmp.command.dialogs;

import de.nikey.combatLog.Combat.CombatManager;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings("UnstableApiUsage")
public class FastTravelListener implements Listener {
    private final Main plugin;
    private final CombatManager combat;
    MiniMessage mm = MiniMessage.miniMessage();

    public FastTravelListener(Main plugin, CombatManager combat) {
        this.plugin = plugin;
        this.combat = combat;
    }

    @EventHandler
    public void onSecretStronghold(PlayerCustomClickEvent e) {
        if (!e.getIdentifier().equals(Key.key("powersmp:fast_travel/secret_stronghold"))) {
            return;
        }
        if (!(e.getCommonConnection() instanceof PlayerGameConnection connection)) {
            return;
        }

        DialogResponseView view = e.getDialogResponseView();
        if (view == null) {
            return;
        }

        Player p = connection.getPlayer();
        if (!(p.hasPermission("powersmp.fast_travel.secret_stronghold"))) return;

        FileConfiguration dungeons = plugin.getDungeonsData();
        String worldName = dungeons.getString("dungeons.secret_stronghold.world");
        double x = dungeons.getDouble("dungeons.secret_stronghold.center.x");
        double y = dungeons.getDouble("dungeons.secret_stronghold.center.y");
        double z = dungeons.getDouble("dungeons.secret_stronghold.center.z");

        assert worldName != null;
        Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);

        if (combat.isInCombat(p)) {
            Component parsed = mm.deserialize(plugin.getConfig().getString("plugin-prefix") + "<red>You are not able to perform this action while in combat!");
            p.sendMessage(parsed);
            return;
        }

        p.teleport(loc);
    }

    @EventHandler
    public void onSpawn(PlayerCustomClickEvent e) {
        if (!e.getIdentifier().equals(Key.key("powersmp:fast_travel/spawn"))) {
            return;
        }
        if (!(e.getCommonConnection() instanceof PlayerGameConnection connection)) {
            return;
        }

        DialogResponseView view = e.getDialogResponseView();
        if (view == null) {
            return;
        }

        Player p = connection.getPlayer();
        if (!(p.hasPermission("powersmp.fast_travel.spawn"))) return;

        Location loc = new Location(Bukkit.getWorld("world"), 61, 75, -219);

        if (combat.isInCombat(p)) {
            Component parsed = mm.deserialize(plugin.getConfig().getString("plugin-prefix") + "<red>You are not able to perform this action while in combat!");
            p.sendMessage(parsed);
            return;
        }

        p.teleport(loc);
    }
}
