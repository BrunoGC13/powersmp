package net.tgc.powersmp.economy.networth;

import org.bukkit.entity.Player;

public class NetWorth {
    private final Player p;
    private double amount;

    public NetWorth(Player p) {
        this.amount = 0;
        this.p = p;
    }

    public NetWorth(Player p, double amount) {
        this.amount = amount;
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

    public double getAmount() {
        return amount;
    }

    public void addWorth(int newAmount) {
        amount += newAmount;
    }

    public void setWorth(int newAmount) {
        amount = newAmount;
    }

    public void removeWorth(int newAmount) {
        amount -= newAmount;
    }
}
