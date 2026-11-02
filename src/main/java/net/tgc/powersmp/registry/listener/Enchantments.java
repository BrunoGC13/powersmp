package net.tgc.powersmp.registry.listener;

import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Enchantments implements Listener {
    // double defaultMovementSpeed = 0.10000000149011612;
    private static final NamespacedKey FAST_WALK_KEY =
            new NamespacedKey("powersmp", "fast_walk_boost");

    private static final double SPEED_PER_LEVEL = 0.08;

    @EventHandler
    public void fastWalk(EntityEquipmentChangedEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;

        Map<EquipmentSlot, EntityEquipmentChangedEvent.EquipmentChange> map = e.getEquipmentChanges();
        if (!map.containsKey(EquipmentSlot.FEET)) return;

        AttributeInstance movementSpeed =
                p.getAttribute(Attribute.MOVEMENT_SPEED);

        if (movementSpeed == null) {
            return;
        }

        AttributeModifier existing = movementSpeed.getModifier(FAST_WALK_KEY);

        if (existing != null) {
            movementSpeed.removeModifier(existing);
        }

        EntityEquipmentChangedEvent.EquipmentChange bootChange = map.get(EquipmentSlot.FEET);

        if (bootChange != null) {
            ItemStack newBoots = bootChange.newItem();

            final Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            final Enchantment fastWalk = registry.get(TypedKey.create(
                    RegistryKey.ENCHANTMENT, Key.key("powersmp:fast_walk"))
            );

            if (fastWalk == null) return;

            Map<Enchantment, Integer> enchants = newBoots.getEnchantments();
            if (enchants.get(fastWalk) != null) {
                int level = newBoots.getEnchantmentLevel(fastWalk);

                if (level <= 0) {
                    return;
                }

                AttributeModifier modifier = new AttributeModifier(
                        FAST_WALK_KEY,
                        SPEED_PER_LEVEL * level,
                        AttributeModifier.Operation.ADD_SCALAR,
                        EquipmentSlotGroup.FEET
                );

                movementSpeed.addModifier(modifier);
            }
        }
    }

}
