package net.tgc.powersmp.item.materials.enforce;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
public class EnforcedDiamond extends CustomItem {
    public EnforcedDiamond(Plugin plugin) {
        super(new NamespacedKey(plugin, "enforced_diamond"));
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.DIAMOND);
        ItemMeta meta = stack.getItemMeta();

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.RARE.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        meta.setEnchantmentGlintOverride(true);
        stack.setItemMeta(meta);

        stack.setData(DataComponentTypes.ITEM_NAME, MiniMessage.miniMessage().deserialize("<blue><bold>Enforced Diamond"));

        return stack;
    }
}
