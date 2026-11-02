package net.tgc.powersmp.dungeon;

import org.bukkit.Location;
import org.bukkit.World;

public class Dungeon {
    private World world;
    private String name;
    private Location center;
    private Location music;
    private double musicStartRadius;
    private String musicId;
    private String boss;
    private int cooldown;
    private double radius; // NEU

    public Dungeon(World world, String name, Location center, Location music,
                   double musicStartRadius, String musicId, String boss,
                   int cooldown, double radius) {
        this.world = world;
        this.name = name;
        this.center = center;
        this.music = music;
        this.musicStartRadius = musicStartRadius;
        this.musicId = musicId;
        this.boss = boss;
        this.cooldown = cooldown;
        this.radius = radius; // NEU
    }

    public double getRadius() { return radius; }

    // alle anderen getter bleiben gleich...
    public double getMusicStartRadius() { return musicStartRadius; }
    public int getCooldown() { return cooldown; }
    public Location getCenter() { return center; }
    public Location getMusic() { return music; }
    public String getBoss() { return boss; }
    public String getMusicId() { return musicId; }
    public String getName() { return name; }
    public World getWorld() { return world; }
}