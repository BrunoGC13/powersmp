package net.tgc.powersmp.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityContext;
import net.tgc.powersmp.api.ability.AbilityTrigger;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * Right-click dash: launches the player in their look direction.
 * Costs 10 power, 2-second cooldown.
 */
public class EmeraldDashAbility implements Ability {
    private static final double DASH_SPEED  = 1.3;
    private static final int    TRAIL_TICKS = 8;

    @Override public String getId()          { return "emerald_dash"; }
    @Override public String getDisplayName() { return "Emerald Dash"; }
    @Override public Set<AbilityTrigger> getTriggers() { return Set.of(AbilityTrigger.RIGHT_CLICK); }
    @Override public double getPowerCost()   { return 10; }
    @Override public long getCooldownTicks() { return 40; }

    @Override
    public void execute(AbilityContext ctx) {
        Player player = ctx.getPlayer();

        // Cancel interact so the player doesn't accidentally place/interact with blocks
        if (ctx.getEvent() instanceof PlayerInteractEvent e) e.setCancelled(true);

        // Launch vector: use look direction, ensure slight upward angle for glide feel
        Vector dir = player.getLocation().getDirection().clone();
        dir.setY(Math.max(dir.getY(), 0.2));
        dir.normalize().multiply(DASH_SPEED);
        player.setVelocity(dir);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.4f);
        player.sendActionBar(Component.text("⚡ Emerald Dash!", NamedTextColor.GREEN));

        // Spawn a green particle trail for TRAIL_TICKS ticks
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!player.isOnline() || ticks++ >= TRAIL_TICKS) { cancel(); return; }
                player.getWorld().spawnParticle(
                        Particle.DUST,
                        player.getLocation().add(0, 1, 0),
                        6, 0.2, 0.3, 0.2, 0,
                        new Particle.DustOptions(Color.fromRGB(0, 220, 80), 1.2f));
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }
}
