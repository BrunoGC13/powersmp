package net.tgc.powersmp.listener;

import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class DungeonMusicStarter implements Listener {
    private Main plugin;
    public DungeonMusicStarter(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!e.hasChangedBlock()) return;

        Player player = e.getPlayer();

        ConfigurationSection dungeonsSection = plugin.getDungeonsData().getConfigurationSection("dungeons");
        if (dungeonsSection == null) return;
        for (String treeKey : dungeonsSection.getKeys(false)) {
            ConfigurationSection dungeon = dungeonsSection.getConfigurationSection(treeKey);
            if (dungeon == null) continue;

            String worldName = dungeon.getString("world", "world");

            if (!player.getWorld().equals(Bukkit.getWorld(worldName))) return;

            double musicX = dungeon.getDouble("music.musicStart.x");
            double musicY = dungeon.getDouble("music.musicStart.y");
            double musicZ = dungeon.getDouble("music.musicStart.z");
            World world = Bukkit.getWorld(worldName);
            Location musicLoc = new Location(world, musicX, musicY, musicZ);
            Location playerLoc = player.getLocation();
            double distance = playerLoc.distance(musicLoc);
            double trigger = dungeon.getDouble("music.musicStartRadius");
            if (distance > trigger) return;

            String customMusic = dungeon.getString("music.music");
            plugin.getLogger().info("playmusic:" + customMusic);
            if (customMusic == null) return;

            player.stopSound(SoundCategory.MUSIC);
            player.playSound(playerLoc, customMusic, SoundCategory.MUSIC, 1.0f, 1.0f);
        }
    }
}
