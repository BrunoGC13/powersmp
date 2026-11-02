package net.tgc.powersmp.item.weapons;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.ability.ArmorersSecretAbility;
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
public class TheArmorersSword extends CustomItem {
    public TheArmorersSword(Plugin plugin) {
        super(new NamespacedKey(plugin, "armorers_sword"), List.of(new ArmorersSecretAbility()));
    }
    MiniMessage mm = MiniMessage.miniMessage();
    Component parsed = mm.deserialize("<dark_red><bold>Armorer's Sword");

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();

        List<Component> lore = new ArrayList<>();
        for (var ability : getAbilities()) {
            lore.add(Component.empty());
            lore.addAll(AbilityLoreHelper.build(ability));
        }

        lore.add(Component.empty());
        lore.add(Component.text(FontIcon.ARTIFACT.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        stack.setItemMeta(meta);

        stack.setData(DataComponentTypes.ITEM_NAME, parsed);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp","armorers_sword"));
        return stack;
    }
}
