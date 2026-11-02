package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class ItemMovementListener implements Listener {

    private final Main plugin;

    public ItemMovementListener(Main plugin) {
        this.plugin = plugin;
    }

    /*
     * Player picks up an item from the ground.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Get the live item stack from the dropped entity
        org.bukkit.entity.Item droppedItem = event.getItem();
        ItemStack item = droppedItem.getItemStack();

        if (!isUsable(item)) {
            return;
        }

        // Apply ownership directly to the item stack before it enters the inventory
        plugin.getItemOwnerShip().changeItemOwnerShip(item, player);

        // Ensure the entity reflects the updated item stack
        droppedItem.setItemStack(item);
    }

    /*
     * Player clicks, shift-clicks, swaps, or transfers items
     * between the top and bottom inventories.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        ItemStack clickedSnapshot =
                isUsable(clickedItem) ? clickedItem.clone() : null;

        ItemStack cursorSnapshot =
                isUsable(cursorItem) ? cursorItem.clone() : null;

        Inventory clickedInventory = event.getClickedInventory();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline()) return;

            // Process items that actually moved into the player's inventory.
            processInventory(player, player.getInventory());

            // Also process items that actually moved into the
            // inventory UI (chest, shulker, custom GUI, etc.).
            // Ownership is delegated to your existing method.
            if (clickedInventory != null
                    && clickedInventory != player.getInventory()) {
                processInventory(player, clickedInventory);
            }

            // Snapshots are available for comparing before/after state
            // if you later want to restrict processing to moved items.
        });
    }

    /*
     * Player drags an item over one or more inventory slots.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Inventory topInventory = event.getView().getTopInventory();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline()) return;

            processInventory(player, player.getInventory());

            if (topInventory != player.getInventory()) {
                processInventory(player, topInventory);
            }
        });
    }

    /*
     * Hopper or another inventory moves an item to another inventory.
     * There may be no player directly involved.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryMove(InventoryMoveItemEvent event) {
        ItemStack movedItem = event.getItem().clone();

        // No player caused this transfer directly, so don't assign
        // ownership to an arbitrary player.
        //
        // The transfer itself can be monitored here if you later
        // maintain a separate item-location registry.
    }

    private void processInventory(Player player, Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (!isUsable(item)) continue;

            plugin.getItemOwnerShip()
                    .changeItemOwnerShip(item, player);
        }
    }

    private boolean isUsable(ItemStack item) {
        return item != null && !item.getType().isAir();
    }
}
