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
 * A crystallized fragment of kinetic energy, extracted from emerald ore under heavy Fortune.
 *
 * How to obtain:
 *   Mine Emerald Ore or Deepslate Emerald Ore with a Diamond or Netherite Pickaxe
 *   enchanted with Fortune III. 50% chance to drop alongside the normal emerald.
 *
 * Item model key: powersmp:kinetic_shard
 * Thematic link: Power Emerald gem, movement/dash abilities.
 */
@SuppressWarnings("UnstableApiUsage")
public class KineticShard extends CustomItem {

    public KineticShard(Plugin plugin) {
        super(new NamespacedKey(plugin, "kinetic_shard"), List.of());
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.EMERALD);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Kinetic Shard", NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.lore(List.of(
                Component.text("Born from the pressure of mountain tides,", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("charged by millennia of tectonic restlessness.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("Only Fortune can coax it from emerald ore.", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("✦ Crafting Material", NamedTextColor.DARK_GREEN)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "kinetic_shard"));
        return stack;
    }
}
