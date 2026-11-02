package net.tgc.powersmp.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.component.DataComponentType;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.other.Helper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AlexGrandmaRelic extends CustomItem {
    public AlexGrandmaRelic(Plugin plugin) {
        super(new NamespacedKey(plugin, "alex_grandma_relic"));
    }
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public ItemStack buildItem() {
        Helper helper = new Helper();
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);

        if (stack.getItemMeta() instanceof EnchantmentStorageMeta meta) {

            meta.addStoredEnchant(Enchantment.SHARPNESS, 6, true);
            meta.addStoredEnchant(Enchantment.UNBREAKING, 5, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text(helper.getLevelIcon(6, "red") + " Sharpness", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text(helper.getLevelIcon(5, "blue") + " Unbreaking", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());

            meta.lore(lore);
            stack.setItemMeta(meta);
        }
        Component parsed = mm.deserialize("<yellow><bold>Grandma's Secret Book");
        stack.setData(DataComponentTypes.ITEM_NAME, parsed);

        return stack;
    }
}
