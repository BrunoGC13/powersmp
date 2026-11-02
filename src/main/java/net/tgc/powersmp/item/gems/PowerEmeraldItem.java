package net.tgc.powersmp.item.gems;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.ability.EmeraldDashAbility;
import net.tgc.powersmp.api.ability.AbilityLoreHelper;
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
public class PowerEmeraldItem extends CustomItem {

    public PowerEmeraldItem(Plugin plugin) {
        super(new NamespacedKey(plugin, "power_emerald"), List.of(new EmeraldDashAbility()));
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.EMERALD);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Power Emerald", NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("An emerald pulsing with kinetic energy.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Apply to a sword in a crafting table", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("to unlock:", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false));

        for (var ability : getAbilities()) {
            lore.add(Component.empty());
            lore.addAll(AbilityLoreHelper.build(ability));
        }

        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.RARE.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "power_emerald"));
        return stack;
    }
}
