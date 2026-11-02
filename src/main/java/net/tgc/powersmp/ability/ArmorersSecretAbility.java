package net.tgc.powersmp.ability;

import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityContext;
import net.tgc.powersmp.api.ability.AbilityTrigger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Set;

public class ArmorersSecretAbility implements Ability {
    @Override
    public String getId() {
        return "armorers_secret_ability";
    }

    @Override
    public String getDisplayName() {
        return "Armorers Secret Ability";
    }

    @Override
    public Set<AbilityTrigger> getTriggers() {
        return Set.of(AbilityTrigger.RIGHT_CLICK);
    }

    @Override
    public double getPowerCost() {
        return 35;
    }

    @Override
    public long getCooldownTicks() {
        return 600;
    }

    private Collection<Player> getNearbyPlayers(Player player, double radius) {
        // Cuboid bounding box
        return player.getLocation().getNearbyPlayers(radius, radius, radius);
    }

    @Override
    public void execute(AbilityContext ctx) {
        Player p = ctx.getPlayer();

        if (ctx.getEvent() instanceof PlayerInteractEvent e) {
            e.setCancelled(true);
        }

        for (Player p2 : getNearbyPlayers(p, 8.0)) {
            if (p2.equals(p)) continue;

            PotionEffect effect =
                    new PotionEffect(PotionEffectType.LEVITATION, 40, 8);

            p2.addPotionEffect(effect);
        }
    }
}
