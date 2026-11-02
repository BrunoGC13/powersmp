package net.tgc.powersmp.item.weapons;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.other.FontIcon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class RubySpear extends CustomItem {
    public RubySpear(Plugin plugin) {
        super(new NamespacedKey(plugin, "ruby_spear"), List.of());
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.DIAMOND_SPEAR);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Ruby Spear", NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, true)
                .decoration(TextDecoration.BOLD, true));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(FontIcon.UNCOMMON.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "ruby_spear"));

        stack.addUnsafeEnchantment(Enchantment.LUNGE, 2);
        stack.addUnsafeEnchantment(Enchantment.SHARPNESS, 2);
        stack.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);

        return stack;
    }
}
