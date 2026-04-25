package net.tgc.powersmp.gems;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.ability.NoCooldownAbility;
import net.tgc.powersmp.api.ability.AbilityLoreHelper;
import net.tgc.powersmp.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class NoCooldownGem extends CustomItem {
    private final Main plugin;

    public NoCooldownGem(Main plugin) {
        super(new NamespacedKey(plugin, "cooldown_gem"), List.of(new NoCooldownAbility(plugin)));
        this.plugin = plugin;
    }

    @Override
    public ItemStack buildItem() {
        ItemStack stack = new ItemStack(Material.REDSTONE);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("No Cooldown Gem", NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Removes attack speed limits temporarily.", NamedTextColor.GRAY)
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

        meta.lore(lore);

        stack.setItemMeta(meta);  // set meta FIRST
        stack.setData(DataComponentTypes.ITEM_MODEL, new NamespacedKey(plugin, "cooldown_gem")); // THEN item_model
        return stack;
    }
}
