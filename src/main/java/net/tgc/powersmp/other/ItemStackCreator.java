package net.tgc.powersmp.other;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ItemStackCreator {
    public ItemStack CustomItemStack(Component name, int amount, Material material) {
        ItemStack stack = new ItemStack(material, amount);
        stack.setData(DataComponentTypes.CUSTOM_NAME, name);
        return stack;
    }

    public static ItemStack buildItem(Material material, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(MiniMessage.miniMessage().deserialize(name));

        List<Component> lore = loreLines.stream()
                .map(line -> MiniMessage.miniMessage().deserialize(line))
                .toList();
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }
}
