package net.tgc.powersmp.item.tools;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.other.FontIcon;
import net.tgc.powersmp.other.Helper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CoalPickaxe extends CustomItem {
    public CoalPickaxe(Main plugin) {
        super(new NamespacedKey(plugin, "coal_pickaxe"));
    }

    @Override
    public ItemStack buildItem() {
        Helper helper = new Helper();

        ItemStack stack = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Coal Pickaxe", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.addEnchant(Enchantment.EFFICIENCY, 2, true);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Forged from coal and iron,", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("surprisingly effective.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text(helper.getLevelIcon(2, "blue") + " Efficiency", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(helper.getLevelIcon(1, "blue") + " Unbreaking", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.COMMON.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "coal_pickaxe"));

        return stack;
    }
}
