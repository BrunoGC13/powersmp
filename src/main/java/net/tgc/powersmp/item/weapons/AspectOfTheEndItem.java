package net.tgc.powersmp.item.weapons;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.ability.EndermanTeleportAbility;
import net.tgc.powersmp.api.ability.AbilityLoreHelper;
import net.tgc.powersmp.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * A blade forged from void energy. Right-click to teleport up to 8 blocks forward.
 *
 * Custom model data: 3001
 *
 * Recipe (3x3):
 *   E E E
 *   E P E
 *   E E E
 * E = Eye of Ender, P = Ender Pearl
 */
public class AspectOfTheEndItem extends CustomItem {

    public AspectOfTheEndItem(Plugin plugin) {
        super(new NamespacedKey(plugin, "aspect_of_the_end"), List.of(new EndermanTeleportAbility()));
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Aspect of the End", NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A blade forged from the void itself.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());

        for (var ability : getAbilities()) {
            lore.addAll(AbilityLoreHelper.build(ability));
            lore.add(Component.empty());
        }

        lore.add(Component.text("Model/Texture by ", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text("@Eftann", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));

        meta.lore(lore);
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        meta.addEnchant(Enchantment.LOOTING, 3, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(3001);
        stack.setItemMeta(meta);
        return stack;
    }
}
