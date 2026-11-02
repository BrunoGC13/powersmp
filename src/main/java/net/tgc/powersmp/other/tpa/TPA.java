package net.tgc.powersmp.other.tpa;

import org.bukkit.entity.Player;

public class TPA {
    private Player sender;
    private Player target;
    private TPAType type;
    private boolean accepted;
    private int id;

    public TPA(Player sender, Player target, TPAType type, int id) {
        this.sender = sender;
        this.target = target;
        this.accepted = false;
        this.type = type;
        this.id = id;
    }

    public Player getSender() {
        return sender;
    }

    public int getId() {
        return id;
    }

    public Player getTarget() {
        return target;
    }

    public TPAType getType() {
        return type;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public TPA setAccepted(boolean accepted) {
        this.accepted = accepted;
        return this;
    }
}
