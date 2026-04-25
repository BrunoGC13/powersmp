package net.tgc.powersmp.ability;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityContext;
import net.tgc.powersmp.api.ability.AbilityTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Set;

public class EscapeAbility implements Ability {
    private final Main plugin;
    public EscapeAbility(Main plugin) {
        this.plugin = plugin;
    }

    @Override public String getId()          { return "escape"; }
    @Override public String getDisplayName() { return "Escape"; }
    @Override public Set<AbilityTrigger> getTriggers() {
        return Set.of(AbilityTrigger.SNEAK_RIGHT_CLICK);
    }
    @Override public double getPowerCost()   { return 60; }
    @Override public long getCooldownTicks() { return 2400; } // 2 minutes (2*60*20=2400),

    @Override
    public void execute(AbilityContext ctx) {
        Player p = ctx.getPlayer();
        if (ctx.getEvent() instanceof PlayerInteractEvent e) e.setCancelled(true);
        Location loc = p.getLocation();
        double radius = 5.0;
        Collection<Player> nearbyPlayers = loc.getNearbyPlayers(radius);
        nearbyPlayers.remove(p);

        for (Player nearby : nearbyPlayers) {
            nearby.sendRichMessage("<gray>[<yellow>PowerSMP<gray>] <white>The player <green> " + p.getName() + " <white> has triggered an Escape gem!");
            PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, 100, 1); // 100 ticks = 5s*20t
            PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, 60, 1); // 60 ticks = 3s*20t
            PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);

            nearby.addPotionEffect(weakness);
            nearby.addPotionEffect(slowness);
            nearby.addPotionEffect(blindness);
        }

        PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, 200, 1); // 200 ticks = 10s*20t
        p.addPotionEffect(glowing);
    }
}
