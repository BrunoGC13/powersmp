package net.tgc.powersmp.sell;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SellResult {
    private final ItemStack item;
    private final int amount;
    private double price;
    private final boolean success;
    private final Component message; // optional
    private final List<ItemStack> items;
    private final List<ItemStack> failedItems;

    public SellResult(ItemStack item, int amount, double price, boolean success) {
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.success = success;
        this.message = null;
        this.items = null;
        this.failedItems = null;
    }

    public SellResult(ItemStack item, int amount, double price, boolean success, Component message) {
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.success = success;
        this.message = message;
        this.items = null;
        this.failedItems = null;
    }

    public SellResult(List<ItemStack> items, double price, boolean success) {
        this.item = null;
        this.amount = -1;
        this.price = price;
        this.success = success;
        this.items = items;
        this.message = null;
        this.failedItems = null;
    }

    public SellResult(List<ItemStack> items, double price, boolean success, Component message) {
        this.item = null;
        this.amount = -1;
        this.price = price;
        this.success = success;
        this.message = message;
        this.items = items;
        this.failedItems = null;
    }

    public SellResult(List<ItemStack> items, List<ItemStack> failedItems, double price, boolean success) {
        this.item = null;
        this.amount = -1;
        this.price = price;
        this.success = success;
        this.items = items;
        this.message = null;
        this.failedItems = failedItems;
    }

    public SellResult(List<ItemStack> items, List<ItemStack> failedItems, double price, boolean success, Component message) {
        this.item = null;
        this.amount = -1;
        this.price = price;
        this.success = success;
        this.message = message;
        this.items = items;
        this.failedItems = failedItems;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public Component getMessage() {
        return message;
    }

    public List<ItemStack> getFailedItems() {
        return failedItems;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setPrice(int newPrice) {
        price = newPrice;
    }
}
