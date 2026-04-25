package net.tgc.powersmp.item.materials;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * The oldest fire in the world, compressed into solid form by the Nether's crushing heat.
 *
 * How to obtain:
 *   Mine Ancient Debris with a Netherite Pickaxe enchanted with Fortune III.
 *   30% chance to drop alongside the normal ancient debris.
 *
 * Item model key: powersmp:primal_ember
 * Thematic link: Fireball ability/item, fire-based weapons.
 */
@SuppressWarnings("UnstableApiUsage")
public class PrimalEmber extends CustomItem {

    public PrimalEmber(Plugin plugin) {
        super(new NamespacedKey(plugin, "primal_ember"), List.of());
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Primal Ember", NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.lore(List.of(
                Component.text("The oldest fire in the world, compressed", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("into solid form by the Nether's crushing heat.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("It never cools. It never fades.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("Even Fortune must work hard to free it.", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("✦ Crafting Material", NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "primal_ember"));
        return stack;
    }
}
