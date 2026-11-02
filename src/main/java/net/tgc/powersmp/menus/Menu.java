package net.tgc.powersmp.menus;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private final List<Button> buttons = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private int size = 9*3;
    private String title = "Test Menu";

    public final List<Button> getButtons() {
        return buttons;
    }
    public final List<Item> getItems() {
        return items;
    }

    protected final void addButton(Button button) {
        this.buttons.add(button);
    }

    protected final void addItem(Item item) {
        this.items.add(item);
    }

    protected final void setSize(int size) {
        this.size = size;
    }

    protected final void setTitle(String title) {
        this.title = title;
    }

    public final void displayTo(Player player) {
        Inventory inv = Bukkit.createInventory(
                player,
                this.size,
                MiniMessage.miniMessage().deserialize(this.title));

        for (Button button : this.buttons) {
            inv.setItem(button.getSlot(), button.getItem());
        }

        for (Item item : this.items) {
            inv.setItem(item.getSlot(), item.getItem());
        }

        player.openInventory(inv);
        player.setMetadata("powersmp", new FixedMetadataValue(Main.getInstance(), this));
    }
}
