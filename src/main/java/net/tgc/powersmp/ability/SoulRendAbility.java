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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

/**
 * Pulls all nearby enemies toward the player, deals void damage, and blinds them briefly.
 * Costs 25 power, 8-second cooldown.
 */
public class SoulRendAbility implements Ability {

    private static final double PULL_RADIUS = 9.0;
    private static final double PULL_SPEED  = 1.8;

    @Override public String getId()          { return "soul_rend"; }
    @Override public String getDisplayName() { return "Soul Rend"; }
    @Override public Set<AbilityTrigger> getTriggers() { return Set.of(AbilityTrigger.SNEAK_RIGHT_CLICK); }
    @Override public double getPowerCost()   { return 25; }
    @Override public long getCooldownTicks() { return 160; }

    @Override
    public void execute(AbilityContext ctx) {
        Player player = ctx.getPlayer();

        if (ctx.getEvent() instanceof PlayerInteractEvent e) e.setCancelled(true);

        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.8f, 0.5f);
        player.sendActionBar(Component.text("☠ Soul Rend!", NamedTextColor.DARK_PURPLE));

        // Expanding void ring effect
        spawnRingEffect(player);

        List<Entity> nearby = player.getNearbyEntities(PULL_RADIUS, PULL_RADIUS / 2.0, PULL_RADIUS);
        for (Entity entity : nearby) {
            if (!(entity instanceof LivingEntity living) || entity == player) continue;

            // Pull toward player
            Vector pull = player.getLocation().toVector()
                    .subtract(entity.getLocation().toVector())
                    .normalize()
                    .multiply(PULL_SPEED);
            pull.setY(Math.max(pull.getY(), 0.25)); // slight lift so they don't get stuck in ground
            entity.setVelocity(pull);

            living.damage(6.0, player);
            living.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0, false, false));

            // Void particle burst on each victim
            entity.getWorld().spawnParticle(
                    Particle.DUST,
                    entity.getLocation().add(0, 1, 0),
                    12, 0.4, 0.5, 0.4, 0,
                    new Particle.DustOptions(Color.fromRGB(80, 0, 120), 1.5f));
        }
    }

    private void spawnRingEffect(Player player) {
        new BukkitRunnable() {
            double radius = 0.5;
            int frame = 0;

            @Override
            public void run() {
                if (frame++ >= 10) { cancel(); return; }

                int points = 24;
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    player.getWorld().spawnParticle(
                            Particle.DUST,
                            player.getLocation().add(x, 0.1, z),
                            1, 0, 0, 0, 0,
                            new Particle.DustOptions(Color.fromRGB(100, 0, 160), 1.2f));
                }
                radius += 0.9;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }
}
