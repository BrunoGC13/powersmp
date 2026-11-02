package net.tgc.powersmp.item;

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

@SuppressWarnings("UnstableApiUsage")
public class HeartItem extends CustomItem {

    public HeartItem(Plugin plugin) {
        super(new NamespacedKey(plugin, "heart_item"));
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = stack.getItemMeta();

        meta.lore(List.of(
            Component.empty(),
            Component.text("Consume to gain ", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text("+1 ❤ Heart", NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false)),
            Component.empty(),
            Component.text("Right-click to use.", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false)
        ));

        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_NAME,
            Component.text("❤ Heart Crystal", NamedTextColor.RED)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false));
        stack.setData(DataComponentTypes.ITEM_MODEL,
            Key.key("powersmp", "heart_item"));
        return stack;
    }
}