package net.tgc.powersmp.ability;

import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityContext;
import net.tgc.powersmp.api.ability.AbilityTrigger;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public class FireballAbility implements Ability {
    @Override public String getId() { return "fireball"; }
    @Override public String getDisplayName() { return "Fireball"; }

    @Override public Set<AbilityTrigger> getTriggers() { return Set.of(AbilityTrigger.RIGHT_CLICK); }

    @Override public double getPowerCost()   { return 20; }
    @Override public long getCooldownTicks() { return 60; } // 3 seconds

    @Override
    public void execute(AbilityContext context) {
        Player p = context.getPlayer();
        if (context.getEvent() instanceof PlayerInteractEvent e) e.setCancelled(true);
        Fireball fb = p.launchProjectile(Fireball.class);
        fb.setShooter(p);
    }
}
