package net.tgc.powersmp.skills;

import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LumberSkill implements Listener {
    private Main plugin;
    private final Set<Material> ores = new HashSet<>(Arrays.asList(
            Material.OAK_LOG,
            Material.SPRUCE_LOG,
            Material.BIRCH_LOG,
            Material.ACACIA_LOG,
            Material.CHERRY_LOG,
            Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG,
            Material.JUNGLE_LOG,
            Material.PALE_OAK_LOG,
            Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG,
            Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_ACACIA_LOG,
            Material.STRIPPED_CHERRY_LOG,
            Material.STRIPPED_DARK_OAK_LOG,
            Material.STRIPPED_MANGROVE_LOG,
            Material.STRIPPED_JUNGLE_LOG,
            Material.STRIPPED_PALE_OAK_LOG
    ));

    private Skill lumberSkill;

    public LumberSkill(Main plugin) {
        this.plugin = plugin;
        this.lumberSkill = plugin.getSkillsRegistry().getSkillData("lumber_skill");
    }

    @EventHandler
    public void onBlockMined(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (ores.contains(block.getType())) {
            plugin.getSkillsManager().addProgress(1, e.getPlayer(), lumberSkill);
        }
    }
}
