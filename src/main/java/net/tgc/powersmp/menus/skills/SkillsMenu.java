package net.tgc.powersmp.menus.skills;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.Button;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.tgc.powersmp.skills.Skill;
import net.tgc.powersmp.skills.SkillsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SkillsMenu extends Menu {
    public SkillsMenu(Main plugin) {
        this.setTitle("Skills");
        this.setSize(9*3);
        int miningSkillSlot = 10;
        int lumberSkillSlot = 13;
        int hunterSkillSlot = 16;
        this.addButton(new Button(miningSkillSlot) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.DIAMOND_PICKAXE, "<gray>Mining Skill", List.of("", "<yellow>-> <white>Manage your Mining Skills"));
            }

            @Override
            public void onClick(Player player) {
                new MiningSkillMenu(plugin, player).displayTo(player);
            }
        });
        this.addButton(new Button(lumberSkillSlot) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.STONE_AXE, "<gray>Lumber Skill", List.of("", "<yellow>-> <white>Manage your Lumber Skills"));
            }

            @Override
            public void onClick(Player player) {
                new LumberSkillMenu(plugin, player).displayTo(player);
            }
        });
        this.addButton(new Button(hunterSkillSlot) {
            @Override
            public ItemStack getItem() {
                return ItemStackCreator.buildItem(Material.IRON_SWORD, "<gray>Hunting Skill", List.of("", "<yellow>-> <white>Manage your Hunting Skills"));
            }

            @Override
            public void onClick(Player player) {
                new HuntingSkillMenu(plugin, player).displayTo(player);
            }
        });
    }
}
