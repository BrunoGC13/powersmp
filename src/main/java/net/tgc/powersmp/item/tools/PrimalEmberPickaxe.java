package net.tgc.powersmp.item.tools;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.ability.EscapeAbility;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.other.FontIcon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PrimalEmberPickaxe extends CustomItem {
    private final Main plugin;
    MiniMessage mm = MiniMessage.miniMessage();

    public PrimalEmberPickaxe(Main plugin) {
        super(new NamespacedKey(plugin, "primal_ember_pickaxe"), List.of());
        this.plugin = plugin;
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.DIAMOND_PICKAXE);

        stack.addEnchantment(Enchantment.EFFICIENCY, 5);
        stack.addEnchantment(Enchantment.UNBREAKING, 2);
        stack.addEnchantment(Enchantment.FORTUNE, 3);

        ItemMeta meta = stack.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.RARE.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);

        stack.setItemMeta(meta);

        stack.setData(
                DataComponentTypes.ITEM_NAME,
                mm.deserialize("<black><bold>Primal Ember Pickaxe")
        );

        return stack;
    }
}
