package net.tgc.powersmp.item.world;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.other.FontIcon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EliasPendant extends CustomItem {
    public EliasPendant(Main plugin) { super(new NamespacedKey(plugin, "elias_pendant"), List.of());}

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public ItemStack buildItem() {
        ItemStack pendant = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = pendant.getItemMeta();

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(FontIcon.ARTIFACT.get() + " " + FontIcon.LEGENDARY.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        pendant.setItemMeta(meta);

        pendant.setData(DataComponentTypes.ITEM_NAME, mm.deserialize("<dark_red><bold>Elias' Pendant"));
        pendant.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "elias_pendant"));
        return pendant;
    }
}
