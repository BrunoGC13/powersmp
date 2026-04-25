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
 * A crystal of pure resonance, fused from amethyst and ancient echo.
 *
 * How to obtain (crafting only):
 *   . A .
 *   A E A    A = Amethyst Shard, E = Echo Shard
 *   . A .
 *
 * Item model key: powersmp:resonite_crystal
 * Thematic link: Power Diamond gem, NoCooldown Gem, warden/sonic abilities.
 */
@SuppressWarnings("UnstableApiUsage")
public class ResoniteCrystal extends CustomItem {

    public ResoniteCrystal(Plugin plugin) {
        super(new NamespacedKey(plugin, "resonite_crystal"), List.of());
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Resonite Crystal", NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.lore(List.of(
                Component.text("Where amethyst meets ancient echo,", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("a crystal of pure resonance is born —", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("humming with the memory of deep sound.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("Fused by hand, not by fortune.", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("✦ Crafting Material", NamedTextColor.DARK_AQUA)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "resonite_crystal"));
        return stack;
    }
}
