package net.tgc.powersmp.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.api.ability.Ability;
import net.tgc.powersmp.api.ability.AbilityLoreHelper;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles applying Power Gem items (PowerEmerald, PowerDiamond, …) to swords in a crafting table.
 *
 * Shapeless — anywhere in the 3×3 grid:
 *   any sword + power gem → that sword with the gem's abilities appended
 *
 * Rules:
 * - Exactly one gem and one sword; any other items cancel the result.
 * - Abilities already on the sword are not duplicated.
 */
public class GemApplicationListener implements Listener {

    @EventHandler
    public void onPrepare(PrepareItemCraftEvent event) {
        CustomItemRegistry registry = CustomItemRegistry.getInstance();
        ItemStack[] matrix = event.getInventory().getMatrix();

        CustomItem gem   = null;
        ItemStack  sword = null;

        for (ItemStack item : matrix) {
            if (item == null || item.getType() == Material.AIR) continue;

            Optional<CustomItem> customItem = registry.fromItemStack(item);
            if (customItem.isPresent() && !customItem.get().getAbilities().isEmpty()) {
                if (gem != null) return;
                gem = customItem.get();
                continue;
            }

            if (item.getType().name().endsWith("_SWORD")) {
                if (sword != null) return;
                sword = item;
                continue;
            }

            return;
        }

        if (gem == null || sword == null) return;

        List<String> existingIds = registry.getAbilityIds(sword);
        ItemStack result = sword.clone();

        // Collect newly added abilities
        List<Ability> added = new ArrayList<>();
        for (Ability ability : gem.getAbilities()) {
            if (existingIds.contains(ability.getId())) continue;
            result = registry.appendAbility(result, ability);
            added.add(ability);
        }

        if (added.isEmpty()) return;

        // Build the new lore block
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        List<Component> lore = meta.hasLore()
                ? new ArrayList<>(meta.lore())
                : new ArrayList<>();

        lore.add(Component.empty());
        lore.add(Component.text("Abilities:", NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false));

        for (Ability ability : added) {
            lore.addAll(AbilityLoreHelper.build(ability));
            lore.add(Component.empty());
        }

        // Trim trailing blank line
        if (!lore.isEmpty() && lore.get(lore.size() - 1).equals(Component.empty())) {
            lore.remove(lore.size() - 1);
        }

        meta.lore(lore);
        result.setItemMeta(meta);
        event.getInventory().setResult(result);
    }
}
