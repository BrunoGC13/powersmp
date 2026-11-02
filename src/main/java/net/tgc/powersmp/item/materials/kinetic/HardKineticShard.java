package net.tgc.powersmp.item.materials.kinetic;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnstableApiUsage")
public class HardKineticShard extends CustomItem {
    MiniMessage mm = MiniMessage.miniMessage();

    public HardKineticShard(Plugin plugin) {
        super(new NamespacedKey(plugin, "hard_kinetic_shard"));
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = CustomItemRegistry.getInstance().createItemStack(new NamespacedKey("powersmp", "kinetic_shard"));
        ItemMeta meta = stack.getItemMeta();
        meta.setEnchantmentGlintOverride(true);

        Component parsed = mm.deserialize("<green><bold>Hard Kinetic Shard");
        meta.displayName(parsed);

        stack.setItemMeta(meta);

        stack.setData(DataComponentTypes.ITEM_NAME, parsed);

        return stack;
    }
}
