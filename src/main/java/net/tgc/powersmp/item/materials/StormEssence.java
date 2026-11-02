package net.tgc.powersmp.item.materials;

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
public class StormEssence extends CustomItem {

    public StormEssence(Plugin plugin) {
        super(new NamespacedKey(plugin, "storm_essence"), List.of());
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Storm Essence", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A crystallised bolt of lightning, frozen", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("mid-strike inside amethyst by forces", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("older than the storm itself.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Fortune pulls the charge free — handle with care.", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, true));
        lore.add(Component.empty());
        lore.add(Component.text("✦ Crafting Material", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.EPIC.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "storm_essence"));
        return stack;
    }
}
