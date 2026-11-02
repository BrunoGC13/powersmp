package net.tgc.powersmp.item.materials.resonite;

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

public class ResoniteCrystalFragment extends CustomItem {
    public ResoniteCrystalFragment(Plugin plugin) {
        super(new NamespacedKey(plugin, "resonite_crystal_fragment"), List.of());
    }
    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Resonite Crystal Fragment", NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.UNCOMMON.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
}
