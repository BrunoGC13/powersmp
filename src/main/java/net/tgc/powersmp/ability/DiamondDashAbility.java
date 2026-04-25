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

import java.util.Set;

/**
 * Right-click dash with a Warden-style sonic boom animation.
 * - Damages all living entities within 1 block left/right and 2.5 blocks forward.
 * - Damage ranges from 4 hearts (far) to 8 hearts (close), scaled by distance.
 * - Costs 25 power, 3-second cooldown.
 */
public class DiamondDashAbility implements Ability {
    private static final double DASH_SPEED     = 1.5;
    private static final double RANGE_FORWARD  = 2.5;  // blocks in front
    private static final double RANGE_SIDE     = 1.0;  // blocks left/right
    private static final double DAMAGE_MAX     = 8.0;  // HP at distance 0 (4 hearts)
    private static final double DAMAGE_MIN     = 4.0;  // HP at max range   (2 hearts)

    @Override public String getId()          { return "diamond_dash"; }
    @Override public String getDisplayName() { return "Diamond Dash"; }
    @Override public Set<AbilityTrigger> getTriggers() { return Set.of(AbilityTrigger.RIGHT_CLICK); }
    @Override public double getPowerCost()   { return 25; }
    @Override public long getCooldownTicks() { return 60; }

    @Override
    public void execute(AbilityContext ctx) {
        Player player = ctx.getPlayer();

        if (ctx.getEvent() instanceof PlayerInteractEvent e) e.setCancelled(true);

        Location origin = player.getLocation().clone();

        // Horizontal forward and right vectors
        Vector forward = origin.getDirection().clone().setY(0).normalize();
        Vector right   = new Vector(-forward.getZ(), 0, forward.getX());

        // 1 — Launch player
        player.setVelocity(forward.clone().multiply(DASH_SPEED).setY(0.25));

        // 2 — Sonic boom sound + particles (Warden-style beam)
        player.getWorld().playSound(origin, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);
        player.getWorld().playSound(origin, Sound.ENTITY_WARDEN_SONIC_CHARGE, 0.8f, 1.2f);
        spawnBeamParticles(player, origin, forward, right);

        player.sendActionBar(Component.text("⚡ Diamond Dash!", NamedTextColor.AQUA));

        // 3 — Damage entities in the hitbox (checked 1 tick after launch so the player has moved)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                damageEntitiesInHitbox(player, origin, forward, right);
            }
        }.runTaskLater(Main.getInstance(), 1L);

        // 4 — Cyan particle trail during the dash
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!player.isOnline() || ticks++ >= 10) { cancel(); return; }
                player.getWorld().spawnParticle(
                        Particle.SCULK_SOUL,
                        player.getLocation().add(0, 1, 0),
                        4, 0.2, 0.3, 0.2, 0.02);
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    // -------------------------------------------------------------------------

    private void spawnBeamParticles(Player player, Location origin, Vector forward, Vector right) {
        Location eyeLoc = origin.clone().add(0, player.getEyeHeight(), 0);

        for (double d = 0.3; d <= RANGE_FORWARD; d += 0.4) {
            // Centre of beam
            Location centre = eyeLoc.clone().add(forward.clone().multiply(d));
            player.getWorld().spawnParticle(Particle.SONIC_BOOM, centre, 1, 0, 0, 0, 0);

            // Wings of the beam (left and right)
            for (double r : new double[]{-RANGE_SIDE, RANGE_SIDE}) {
                Location wing = centre.clone().add(right.clone().multiply(r * (d / RANGE_FORWARD)));
                player.getWorld().spawnParticle(Particle.SCULK_SOUL, wing, 1, 0.05, 0.05, 0.05, 0);
            }
        }
    }

    private void damageEntitiesInHitbox(Player player, Location origin, Vector forward, Vector right) {
        // Generous bounding box fetch — we filter precisely below
        double maxRange = Math.sqrt(RANGE_FORWARD * RANGE_FORWARD + RANGE_SIDE * RANGE_SIDE);

        for (Entity entity : origin.getWorld().getNearbyEntities(origin, RANGE_FORWARD, 2.5, RANGE_SIDE + 0.5)) {
            if (!(entity instanceof LivingEntity target)) continue;
            if (target.equals(player)) continue;

            Vector toEntity = entity.getLocation().toVector().subtract(origin.toVector());

            double fwd = toEntity.dot(forward);       // projection onto forward axis
            double rgt = Math.abs(toEntity.dot(right)); // projection onto side axis

            // Must be inside the hitbox box
            if (fwd < 0 || fwd > RANGE_FORWARD) continue;
            if (rgt > RANGE_SIDE) continue;

            // Distance from player within the hitbox (2D in the horizontal plane)
            double dist   = Math.sqrt(fwd * fwd + rgt * rgt);
            double t      = dist / maxRange;          // 0 = closest, 1 = farthest
            double damage = DAMAGE_MAX - t * (DAMAGE_MAX - DAMAGE_MIN); // lerp 8→4

            target.damage(damage, player);

            // Small impact particle burst on the target
            target.getWorld().spawnParticle(
                    Particle.SONIC_BOOM,
                    target.getLocation().add(0, 1, 0),
                    1, 0, 0, 0, 0);
        }
    }
}
