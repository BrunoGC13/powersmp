package net.tgc.powersmp.menus;

import net.kyori.adventure.text.Component;
import net.tgc.powersmp.other.ItemStackCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TestMenu extends Menu {
    public TestMenu() {
        this.addButton(new Button(9+5) {
            @Override
            public ItemStack getItem() {
                return new ItemStackCreator().CustomItemStack(Component.text("Hi"), 1, Material.ARROW);
            }

            @Override
            public void onClick(Player player) {
                new ListPlayersMenu().displayTo(player);
            }
        });
        this.setTitle("Test Menu");
    }


    private class ListPlayersMenu extends Menu {
        public ListPlayersMenu() {
            this.setSize(9*6);
            this.setTitle("Online Players List");
            int startingSlot = 0;

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                this.addButton(new Button(startingSlot++) {
                    @Override
                    public ItemStack getItem() {
                        return new ItemStackCreator().CustomItemStack(Component.text(onlinePlayer.getName()), 1, Material.PLAYER_HEAD);
                    }

                    @Override
                    public void onClick(Player player) {
                        player.sendMessage("You clicked player " + onlinePlayer.getName());
                    }
                });
            }
        }
    }
}
