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

public class MinerSkill implements Listener {
    private Main plugin;
    private final Set<Material> ores = new HashSet<>(Arrays.asList(
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.COAL_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.COPPER_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.LAPIS_ORE,
            Material.DEEPSLATE_LAPIS_ORE
    ));

    private Skill minerSkill;

    public MinerSkill(Main plugin) {
        this.plugin = plugin;
        this.minerSkill = plugin.getSkillsRegistry().getSkillData("mining_skill");
    }

    @EventHandler
    public void onBlockMined(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (ores.contains(block.getType())) {
            plugin.getSkillsManager().addProgress(1, e.getPlayer(), minerSkill);
        }
    }
}
