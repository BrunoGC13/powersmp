package net.tgc.powersmp.other;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class AFKManager implements Listener {
    private final Set<UUID> afkPlayers = new HashSet<>();
    private MiniMessage mm = MiniMessage.miniMessage();
    private Main plugin;
    private String prefix;

    public AFKManager(Main plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    public void setAfkPlayer(Player p, boolean afk) {
        if (p == null) return;

        if (afk) {
            afkPlayers.add(p.getUniqueId());
            p.sendMessage(mm.deserialize(prefix + "<white>Set AFK mode to <green>on."));
        }
        else {
            afkPlayers.remove(p.getUniqueId());
            p.sendMessage(mm.deserialize(prefix + "<white>Set AFK mode to <red>off."));
        }
    }

    public boolean checkPlayer(Player p) {
        if (p == null) return false;
        return afkPlayers.contains(p.getUniqueId());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;

        if (afkPlayers.contains(p.getUniqueId())) {
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) return;

            if (e.getDamageSource().getCausingEntity() instanceof Player damager) {
                damager.sendMessage(mm.deserialize(prefix + "<red>This player is currently in AFK mode!"));
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInput(PlayerInputEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!afkPlayers.contains(uuid)) {
            return;
        }

        if (event.getInput().isForward()
                || event.getInput().isBackward()
                || event.getInput().isLeft()
                || event.getInput().isRight()
                || event.getInput().isJump()
                || event.getInput().isSneak()) {
            setAfkPlayer(player, false);
        }

        setAfkPlayer(player, false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        afkPlayers.remove(uuid);
    }

}
