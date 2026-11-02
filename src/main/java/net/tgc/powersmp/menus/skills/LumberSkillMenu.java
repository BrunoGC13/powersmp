package net.tgc.powersmp.menus.skills;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.item.PlayerHeadItem;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.menus.Item;
import net.tgc.powersmp.menus.Menu;
import net.tgc.powersmp.other.ItemStackCreator;
import net.tgc.powersmp.skills.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class LumberSkillMenu extends Menu {
    private Main plugin;
    public LumberSkillMenu(Main plugin, Player player) {
        this.plugin = plugin;
        this.setTitle("Lumber Skill");
        this.setSize(9*6);

        int slotInterface = 31;
        int playerHeadSlot = 13;
        this.addItem(new Item(slotInterface) {
            @Override
            public ItemStack getItem() {
                Skill lumberSkill = plugin.getSkillsRegistry().getSkillData("lumber_skill");
                return ItemStackCreator.buildItem(Material.IRON_SWORD, "<gray>Level <white>" + plugin.getSkillsManager().getLevel(player, lumberSkill), List.of(
                        "",
                        "<white>Progress: <gray>" + plugin.getSkillsManager().getProgress(player, lumberSkill),
                        "<white>Target: <gray>" + plugin.getSkillsManager().getTarget(player, lumberSkill),
                        ""
                ));
            }
        });

        this.addItem(new Item(playerHeadSlot) {
            @Override
            public ItemStack getItem() {
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();

                playerHead.setItemMeta(playerHeadMeta);

                playerHeadMeta.setOwningPlayer(player);
                playerHead.setData(DataComponentTypes.ITEM_NAME,
                        Component.text(player.getName(), NamedTextColor.YELLOW)
                                .decoration(TextDecoration.BOLD, true)
                                .decoration(TextDecoration.ITALIC, false));
                return playerHead;
            }
        });
    }
}
