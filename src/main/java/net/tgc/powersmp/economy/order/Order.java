
package net.tgc.powersmp.economy.order;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Order {
    private final long id;
    private final ItemStack item;
    private final int amount;
    private final int priceEach;
    private final long cost;
    private int delivered;
    private final UUID buyer;

    public Order(long id, ItemStack item, int amount,
                 int priceEach, UUID buyer, int delivered) {
        this.id = id;
        this.item = item.clone();
        this.amount = amount;
        this.priceEach = priceEach;
        this.cost = (long) priceEach * amount;
        this.delivered = delivered;
        this.buyer = buyer;
    }

    public long getId() {
        return id;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public int getAmount() {
        return amount;
    }

    public long getCost() {
        return cost;
    }

    public int getDelivered() {
        return delivered;
    }

    public int getRemaining() {
        return amount - delivered;
    }

    public int getPriceEach() {
        return priceEach;
    }

    public UUID getBuyer() {
        return buyer;
    }

    public void deliver(int amount) {
        if (amount <= 0 || amount > getRemaining()) {
            throw new IllegalArgumentException(
                    "Invalid delivery amount"
            );
        }

        delivered += amount;
    }

    public boolean isComplete() {
        return delivered >= amount;
    }
}
