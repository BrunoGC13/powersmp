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
 * A sliver of the void made solid, harvested from End Stone with a Silk Touch Netherite Pickaxe.
 *
 * How to obtain:
 *   Mine End Stone in The End with a Netherite Pickaxe enchanted with Silk Touch.
 *   20% chance to drop — the void doesn't give itself up easily.
 *
 * Item model key: powersmp:void_fragment
 * Thematic link: Aspect of the End, void/teleport abilities.
 */
@SuppressWarnings("UnstableApiUsage")
public class VoidFragment extends CustomItem {

    public VoidFragment(Plugin plugin) {
        super(new NamespacedKey(plugin, "void_fragment"), List.of());
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Void Fragment", NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.lore(List.of(
                Component.text("A sliver of the void made solid,", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("crystallized at the boundary between", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("existence and the endless nothing beyond.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("Silk Touch reveals what Fortune cannot.", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("✦ Crafting Material", NamedTextColor.DARK_PURPLE)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "void_fragment"));
        return stack;
    }
}
