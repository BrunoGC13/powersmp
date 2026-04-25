package net.tgc.powersmp.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.ability.FireballAbility;
import net.tgc.powersmp.api.ability.AbilityLoreHelper;
import net.tgc.powersmp.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FireballItem extends CustomItem {
    public FireballItem(Main plugin) {
        super(new NamespacedKey(plugin, "fire_sword"), List.of(new FireballAbility()));
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Fire Sword", NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A burning charge of infernal energy.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());

        for (var ability : getAbilities()) {
            lore.addAll(AbilityLoreHelper.build(ability));
            lore.add(Component.empty());
        }

        // trim trailing blank line
        if (!lore.isEmpty() && lore.get(lore.size() - 1).equals(Component.empty())) {
            lore.remove(lore.size() - 1);
        }

        meta.lore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
}
