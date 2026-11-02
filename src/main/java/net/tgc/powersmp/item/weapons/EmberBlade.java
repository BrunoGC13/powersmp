package net.tgc.powersmp.item.weapons;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.ability.InfernoSurgeAbility;
import net.tgc.powersmp.api.ability.AbilityLoreHelper;
import net.tgc.powersmp.api.item.CustomItem;
import net.tgc.powersmp.other.FontIcon;
import net.tgc.powersmp.other.Helper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EmberBlade extends CustomItem {

    public EmberBlade(Plugin plugin) {
        super(new NamespacedKey(plugin, "ember_blade"), List.of(new InfernoSurgeAbility()));
    }

    @Override
    public ItemStack buildItem() {
        Helper helper = new Helper();

        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Ember Blade", NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.addEnchant(Enchantment.SHARPNESS, 4, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        meta.addEnchant(Enchantment.UNBREAKING, 3, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Forged in the heart of a dying volcano,", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("every swing carries a breath of primal fire.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text(helper.getLevelIcon(4, "red") + " Sharpness", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(helper.getLevelIcon(2, "red") + " Fire Aspect", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(helper.getLevelIcon(3, "blue") + " Unbreaking", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());

        for (var ability : getAbilities()) {
            lore.addAll(AbilityLoreHelper.build(ability));
            lore.add(Component.empty());
        }

        lore.add(Component.text(FontIcon.EPIC.get() + " " + FontIcon.ARTIFACT.get(), NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        stack.setItemMeta(meta);
        stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "ember_blade"));
        return stack;
    }
}
