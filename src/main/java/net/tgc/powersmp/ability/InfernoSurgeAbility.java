package net.tgc.powersmp.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityContext;
import net.tgc.powersmp.api.ability.AbilityTrigger;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Launches a traveling bolt of fire in the player's look direction.
 * Ignites and damages every entity it passes through (each hit once).
 * Costs 15 power, 5-second cooldown.
 */
public class InfernoSurgeAbility implements Ability {

    private static final int    RANGE_BLOCKS = 16;
    private static final double HIT_RADIUS   = 1.5;
    private static final int    FIRE_TICKS   = 100; // 5 seconds

    @Override public String getId()          { return "inferno_surge"; }
    @Override public String getDisplayName() { return "Inferno Surge"; }
    @Override public Set<AbilityTrigger> getTriggers() { return Set.of(AbilityTrigger.RIGHT_CLICK); }
    @Override public double getPowerCost()   { return 15; }
    @Override public long getCooldownTicks() { return 100; }

    @Override
    public void execute(AbilityContext ctx) {
        Player player = ctx.getPlayer();

        if (ctx.getEvent() instanceof PlayerInteractEvent e) e.setCancelled(true);

        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.8f);
        player.sendActionBar(Component.text("🔥 Inferno Surge!", NamedTextColor.GOLD));

        Vector dir = player.getLocation().getDirection().normalize();
        Location origin = player.getEyeLocation();
        Set<UUID> hit = new HashSet<>();

        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                if (step > RANGE_BLOCKS) { cancel(); return; }

                Location point = origin.clone().add(dir.clone().multiply(step));

                // Stop at solid blocks
                if (step > 0 && point.getBlock().getType().isSolid()) {
                    spawnImpact(point);
                    cancel();
                    return;
                }

                // Particles along the bolt
                point.getWorld().spawnParticle(Particle.FLAME, point, 6, 0.15, 0.15, 0.15, 0.04);
                point.getWorld().spawnParticle(Particle.SMOKE, point, 2, 0.1, 0.1, 0.1, 0.02);

                // Hit entities near this point
                for (Entity entity : point.getWorld().getNearbyEntities(point, HIT_RADIUS, HIT_RADIUS, HIT_RADIUS)) {
                    if (entity == player || !(entity instanceof LivingEntity living)) continue;
                    if (!hit.add(entity.getUniqueId())) continue; // each entity hit once
                    living.setFireTicks(FIRE_TICKS);
                    living.damage(4.0, player);
                    entity.getWorld().spawnParticle(Particle.LAVA, entity.getLocation().add(0, 1, 0),
                            8, 0.3, 0.3, 0.3, 0.1);
                }

                step++;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    private void spawnImpact(Location loc) {
        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 1, 0, 0, 0, 0);
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 20, 0.4, 0.4, 0.4, 0.08);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.4f);
    }
}
