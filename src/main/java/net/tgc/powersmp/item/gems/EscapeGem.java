package net.tgc.powersmp.item.gems;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.ability.EscapeAbility;
import net.tgc.powersmp.api.ability.AbilityLoreHelper;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.other.FontIcon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EscapeGem extends CustomItem {
    private final Main plugin;

    public EscapeGem(Main plugin) {
        super(new NamespacedKey(plugin, "escape_gem"), List.of(new EscapeAbility(plugin)));
        this.plugin = plugin;
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Escape Gem", NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A gem that helps you escape danger.", NamedTextColor.GRAY)
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
        stack.setData(DataComponentTypes.ITEM_MODEL, new NamespacedKey(plugin, "escape_gem"));
        return stack;
    }
}
