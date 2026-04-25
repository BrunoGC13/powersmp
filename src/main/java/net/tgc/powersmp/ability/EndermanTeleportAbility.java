package net.tgc.powersmp.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityContext;
import net.tgc.powersmp.api.ability.AbilityTrigger;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * Teleports the player up to 8 blocks in their horizontal look direction,
 * stopping before any solid block. Spawns enderman particles at both ends.
 */
public class EndermanTeleportAbility implements Ability {
    private static final double MAX_DISTANCE = 8.0;

    @Override public String getId()          { return "instant_transmission"; }
    @Override public String getDisplayName() { return "Instant Transmission"; }
    @Override public Set<AbilityTrigger> getTriggers() { return Set.of(AbilityTrigger.RIGHT_CLICK); }
    @Override public double getPowerCost()   { return 15; }
    @Override public long getCooldownTicks() { return 60; }

    @Override
    public void execute(AbilityContext ctx) {
        Player player = ctx.getPlayer();
        if (ctx.getEvent() instanceof PlayerInteractEvent e) e.setCancelled(true);

        Location from = player.getLocation().clone();
        Vector dir = from.getDirection().clone().setY(0).normalize();

        // Step forward until we hit a solid block or reach max distance
        Location destination = from.clone();
        for (double d = 0.5; d <= MAX_DISTANCE; d += 0.5) {
            Location feet = from.clone().add(dir.clone().multiply(d));
            Location head = feet.clone().add(0, 1, 0);
            if (feet.getBlock().getType().isSolid() || head.getBlock().getType().isSolid()) break;
            destination = feet;
        }

        // Preserve look direction
        destination.setYaw(from.getYaw());
        destination.setPitch(from.getPitch());

        // Particles + sound at departure
        from.getWorld().spawnParticle(Particle.PORTAL, from.clone().add(0, 1, 0), 40, 0.3, 0.5, 0.3, 0.1);
        from.getWorld().playSound(from, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        player.teleport(destination);

        // Particles at arrival
        destination.getWorld().spawnParticle(Particle.PORTAL, destination.clone().add(0, 1, 0), 40, 0.3, 0.5, 0.3, 0.1);

        player.sendActionBar(Component.text("✦ Instant Transmission!", NamedTextColor.DARK_PURPLE));
    }
}
