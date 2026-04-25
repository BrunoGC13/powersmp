package net.tgc.powersmp.ability;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityContext;
import net.tgc.powersmp.api.ability.AbilityTrigger;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public class NoCooldownAbility implements Ability {
    private final Main plugin;
    public NoCooldownAbility(Main plugin) {
        this.plugin = plugin;
    }

    @Override public String getId()          { return "cooldown"; }
    @Override public String getDisplayName() { return "Cooldown"; }
    @Override public Set<AbilityTrigger> getTriggers() {
        return Set.of(AbilityTrigger.RIGHT_CLICK);
    }
    @Override public double getPowerCost()   { return 80; }
    @Override public long getCooldownTicks() { return 2400; } // 2 minutes (2*60*20=2400),

    @Override
    public void execute(AbilityContext ctx) {
        Player player = ctx.getPlayer();
        // Cancel the interact event to avoid accidental block placement
        if (ctx.getEvent() instanceof PlayerInteractEvent e) e.setCancelled(true);

        AttributeInstance attribute = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attribute != null) {
            attribute.setBaseValue(100);

            // 2. Schedule a task to reset it after 3 seconds (60 ticks)
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                attribute.setBaseValue(4.0);
            }, 60L); // 20 ticks = 1 second
        }
    }
}
