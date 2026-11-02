package net.tgc.powersmp.menus.dungeons;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.Item;
import net.tgc.powersmp.menus.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("UnstableApiUsage")
public class SecretStrongholdMenu extends Menu {
    private Main plugin;
    public SecretStrongholdMenu(Main plugin) {
        this.plugin = plugin;
        this.setTitle("Secret Stronghold");
        this.setSize(9 * 6);
        int playerHeadSlot = 14;
        this.addItem(new Item(14) {
            @Override
            public ItemStack getItem() {
                ItemStack stack = new ItemStack(Material.ECHO_SHARD);
                ItemMeta meta = stack.getItemMeta();
                meta.displayName(Component.text("Secret Stronghold", NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true));
                stack.setItemMeta(meta);
                stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("powersmp", "resonite_crystal"));
                return stack;
            }
        });
    }
}
