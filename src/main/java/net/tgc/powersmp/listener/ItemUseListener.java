package net.tgc.powersmp.listener;

import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityContext;
import net.tgc.powersmp.api.ability.AbilityRegistry;
import net.tgc.powersmp.api.ability.AbilityTrigger;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.player.PlayerCooldownManager;
import net.tgc.powersmp.player.PowerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemUseListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        AbilityTrigger trigger = switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK ->
                    player.isSneaking() ? AbilityTrigger.SNEAK_RIGHT_CLICK : AbilityTrigger.RIGHT_CLICK;
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK ->
                    player.isSneaking() ? AbilityTrigger.SNEAK_LEFT_CLICK : AbilityTrigger.LEFT_CLICK;
            default -> null;
        };

        if (trigger != null) triggerAbilities(player, item, trigger, event);
    }

    @EventHandler
    public void onHitEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        triggerAbilities(player, item, AbilityTrigger.HIT_ENTITY, event);
    }

    private void triggerAbilities(Player player, ItemStack item, AbilityTrigger trigger, Event event) {
        List<String> abilityIds = CustomItemRegistry.getInstance().getAbilityIds(item);
        if (abilityIds.isEmpty()) return;

        AbilityRegistry registry = AbilityRegistry.getInstance();
        PlayerCooldownManager cooldowns = PlayerCooldownManager.getInstance();

        for (String id : abilityIds) {
            Ability ability = registry.get(id);
            if (ability == null || !ability.getTriggers().contains(trigger)) continue;
            if (cooldowns.isOnCooldown(player, id)) continue;
            if (!PowerManager.consumePower(player, ability.getPowerCost())) continue;

            ability.execute(new AbilityContext(player, trigger, event));

            if (ability.getCooldownTicks() > 0) {
                cooldowns.setCooldown(player, id, ability.getCooldownTicks());
            }
        }
    }
}
