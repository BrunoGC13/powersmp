package net.tgc.powersmp.civilization;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Civilization {
    private String name;
    private Player owner;
    private Location location;

    public Civilization(String name, Player owner, Location location) {
        this.name = name;
        this.owner = owner;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Player getOwner() {
        return owner;
    }
}
