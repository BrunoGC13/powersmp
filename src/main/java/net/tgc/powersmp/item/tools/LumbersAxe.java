package net.tgc.powersmp.item.tools;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnstableApiUsage")
public class LumbersAxe extends CustomItem {
    MiniMessage mm = MiniMessage.miniMessage();

    public LumbersAxe(Plugin plugin) {
        super(new NamespacedKey(plugin, "lumbers_axe"));
    }

    @Override
    public ItemStack buildItem() {
        ItemStack lumbersAxe = new ItemStack(Material.IRON_AXE);

        lumbersAxe.addEnchantment(Enchantment.EFFICIENCY, 4);
        lumbersAxe.addUnsafeEnchantment(Enchantment.UNBREAKING, 4);
        lumbersAxe.addEnchantment(Enchantment.SHARPNESS, 4);

        lumbersAxe.setData(DataComponentTypes.ITEM_NAME, mm.deserialize("<dark_green><bold>Lumber's Axe"));
        return lumbersAxe;
    }
}
