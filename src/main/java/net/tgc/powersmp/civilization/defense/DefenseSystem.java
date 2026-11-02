package net.tgc.powersmp.civilization.defense;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.civilization.Civilization;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class DefenseSystem {
    private Main plugin;
    private Map<UUID, BukkitRunnable> defenseTasks;

    public DefenseSystem(Main plugin) {
        this.plugin = plugin;
        this.defenseTasks = new HashMap<>();
    }

    /**
     * Sets up defense system for a civilization with archer towers
     */
    public void setupDefense(Civilization civ) {
        // Create defense towers at the borders of the civilization
        createDefenseTowers(civ);

        // Start monitoring for enemy players
        startDefenseMonitoring(civ);
    }

    /**
     * Creates archer towers at civilization borders
     */
    private void createDefenseTowers(Civilization civ) {
        // This would typically be called when setting up the civilization
        // For now, we'll just mark that this civilization has defenses
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".has_defenses",
                true);
        plugin.saveCivilizationsData();
    }

    /**
     * Starts monitoring for enemy players near civilization borders
     */
    private void startDefenseMonitoring(Civilization civ) {
        UUID taskId = UUID.randomUUID();

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                checkForEnemies(civ);
            }
        };

        // Run every 2 seconds
        task.runTaskTimer(plugin, 0L, 40L);
        defenseTasks.put(taskId, task);
    }

    /**
     * Checks for enemy players near civilization borders and attacks them
     */
    private void checkForEnemies(Civilization civ) {
        // Get all online players - convert Collection to List
        Collection<? extends Player> playerCollection = Bukkit.getOnlinePlayers();
        List<Player> onlinePlayers = new ArrayList<>(playerCollection);

        // Get civilization members to avoid attacking allies
        List<UUID> civMembers = plugin.getCivilizationRegistry().getMembers(civ);

        // Get civilization border coordinates (simplified - you might want to implement this properly)
        Location center = civ.getLocation();
        double radius = 50.0; // Adjust based on your needs

        for (Player player : onlinePlayers) {
            // Skip if player is in the same civilization
            if (isInCivilization(player, civMembers)) continue;

            // Check if player is near the civilization border
            if (isNearBorder(player, center, radius)) {
                // Attack with archers
                attackWithArchers(player, civ);
            }
        }
    }

    /**
     * Checks if player is in the same civilization
     */
    private boolean isInCivilization(Player player, List<UUID> civMembers) {
        return civMembers.contains(player.getUniqueId());
    }

    /**
     * Checks if player is near civilization border
     */
    private boolean isNearBorder(Player player, Location center, double radius) {
        double distance = player.getLocation().distance(center);
        // Define border as 40-50 blocks from center
        return distance > (radius - 10) && distance < (radius + 10);
    }

    /**
     * Attacks player with archers from civilization
     */
    private void attackWithArchers(Player target, Civilization civ) {
        // Get civilization border locations (simplified)
        Location center = civ.getLocation();
        double radius = 50.0;

        // Create archer towers around the border
        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;

            Location towerLocation = new Location(center.getWorld(), x, center.getY() + 50, z);

            // Spawn archer at tower location
            spawnArcher(towerLocation, target);
        }
    }

    /**
     * Spawns an archer that shoots at the target
     */
    private void spawnArcher(Location location, Player target) {
        // For simplicity, we'll just shoot arrows directly
        // In a real implementation, you'd spawn actual archer entities

        // Shoot arrow towards player
        Location playerLocation = target.getLocation();
        Location shooterLocation = location.clone().add(0, 1.5, 0); // Adjust for archer height

        Vector direction =
                playerLocation.toVector().subtract(shooterLocation.toVector()).normalize();
        direction.multiply(2); // Adjust arrow speed

        // Create arrow
        Arrow arrow = location.getWorld().spawnArrow(shooterLocation, direction, 2.0F, 10.0F);

        // Add some damage
        arrow.setDamage(5.0);

        // Remove arrow after 5 seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (arrow.isValid()) {
                arrow.remove();
            }
        }, 100L);
    }

    /**
     * Stops defense monitoring for a civilization
     */
    public void stopDefense(Civilization civ) {
        // Remove monitoring tasks
        for (UUID taskId : new ArrayList<>(defenseTasks.keySet())) {
            BukkitRunnable task = defenseTasks.get(taskId);
            if (task != null) {
                task.cancel();
            }
            defenseTasks.remove(taskId);
        }
    }

    /**
     * Sets up defense system for all civilizations
     */
    public void setupAllDefenses() {
        // This would be called when plugin starts or when civilizations are loaded
        // For now, we'll just set up a basic system
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Setup defenses for all civilizations with defenses enabled
            // This is a simplified approach - you'd want to load from config
        }, 20L);
    }

    /**
     * Creates a more advanced defense system with multiple defense types
     */
    public void setupAdvancedDefense(Civilization civ) {
        // Create different types of defenses
        createArcheryTowers(civ);
        createCannonTowers(civ);
        createMagicTowers(civ);

        // Start monitoring
        startDefenseMonitoring(civ);
    }

    private void createArcheryTowers(Civilization civ) {
        // Create archer towers
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".archery_towers",
                true);
        plugin.saveCivilizationsData();
    }

    private void createCannonTowers(Civilization civ) {
        // Create cannon towers
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".cannon_towers",
                true);
        plugin.saveCivilizationsData();
    }

    private void createMagicTowers(Civilization civ) {
        // Create magic towers
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".magic_towers",
                true);
        plugin.saveCivilizationsData();
    }
}