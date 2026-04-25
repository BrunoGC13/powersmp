package net.tgc.powersmp.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.ability.DiamondDashAbility;
import net.tgc.powersmp.api.ability.AbilityLoreHelper;
import net.tgc.powersmp.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * A craftable gem that upgrades any sword with the Diamond Dash ability.
 * Combine in a crafting table: Power Diamond + any sword → sword gains Diamond Dash.
 *
 * Custom model data: 2002 — override assets/minecraft/models/item/diamond.json in your resource pack.
 *
 * Recipe (3x3):
 *   . D .
 *   D D D
 *   . D .
 * D = Diamond
 */
public class PowerDiamondItem extends CustomItem {

    public PowerDiamondItem(Plugin plugin) {
        super(new NamespacedKey(plugin, "power_diamond"), List.of(new DiamondDashAbility()));
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.DIAMOND);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Power Diamond", NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A diamond resonating with warden energy.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Apply to a sword in a crafting table", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("to unlock:", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false));

        for (var ability : getAbilities()) {
            lore.add(Component.empty());
            lore.addAll(AbilityLoreHelper.build(ability));
        }

        meta.lore(lore);
        meta.setCustomModelData(2002);
        stack.setItemMeta(meta);
        return stack;
    }
}
