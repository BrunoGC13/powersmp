package net.tgc.powersmp.item.materials.kinetic;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.other.FontIcon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

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

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Born from the pressure of mountain tides,", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("charged by millennia of tectonic restlessness.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Only Fortune can coax it from emerald ore.", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, true));
        lore.add(Component.empty());
        lore.add(Component.text("✦ Crafting Material", NamedTextColor.DARK_GREEN)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.UNCOMMON.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "kinetic_shard"));
        return stack;
    }
}
