package net.tgc.powersmp.economy.order;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class OrderDialogListener implements Listener {

    private final Main plugin;
    private final OrderManager manager;
    private String prefix;
    MiniMessage mm = MiniMessage.miniMessage();

    public OrderDialogListener(Main plugin, OrderManager manager) {
        this.manager = manager;
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("plugin-prefix");
    }

    @EventHandler
    public void onMaterialSelect(PlayerCustomClickEvent event) {
        // Only handle our material-selection buttons
        if (!event.getIdentifier().equals(
                Key.key("powersmp", "order/select/material"))) {
            return;
        }

        // Get the player
        if (!(event.getCommonConnection()
                instanceof PlayerGameConnection connection)) {
            return;
        }

        Player player = connection.getPlayer();

        // Read the custom payload
        if (event.getTag() == null) {
            return;
        }

        try {
            BinaryTagHolder tagHolder = event.getTag();
            String snbt = tagHolder.string();
            CompoundBinaryTag tag = TagStringIO.builder().build().asCompound(snbt);

            String materialName = tag.getString("material");

            Material material = Material.matchMaterial(materialName);

            if (material == null || material.isAir()) {
                return;
            }

            // We now know exactly which material was selected!
            player.showDialog(new PriceAndMenuDialog(material, manager).getDialog());

        } catch (Exception exception) {
            player.sendMessage("Could not read the selected material.");
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void onOrderFinish(PlayerCustomClickEvent event) {
        // Only handle our material-selection buttons
        if (!event.getIdentifier().equals(
                Key.key("powersmp", "order/finish"))) {
            return;
        }

        // Get the player
        if (!(event.getCommonConnection()
                instanceof PlayerGameConnection connection)) {
            return;
        }

        Player player = connection.getPlayer();

        DialogResponseView view = event.getDialogResponseView();
        if (view == null) {
            return;
        }

        int amount = Integer.parseInt(view.getText("order_amount"));
        int priceEach = Integer.parseInt(view.getText("order_price_each"));

        // Read the custom payload
        if (event.getTag() == null) {
            return;
        }

        try {
            BinaryTagHolder tagHolder = event.getTag();
            String snbt = tagHolder.string();
            CompoundBinaryTag tag = TagStringIO.builder().build().asCompound(snbt);

            String materialName = tag.getString("material");

            Material material = Material.matchMaterial(materialName);

            if (material == null || material.isAir()) {
                return;
            }

            // We now know exactly which material was selected!
            OrderResult result = manager.createOrder(player, new ItemStack(material), amount, priceEach);
            if (result.getType() == OrderResultType.ERROR) {
                player.sendMessage(mm.deserialize(prefix + "<red>" + result.getMsg()));
                return;
            }

            Component name = result.getOrder().getItem().displayName();
            Component message = mm.deserialize(prefix + "<white>Ordered <green>" + result.getOrder().getAmount() + "x ")
                    .append(name)
                    .append(mm.deserialize("<white> for <yellow>" + result.getOrder().getCost()));

            player.sendMessage(message);

        } catch (Exception exception) {
            player.sendMessage("Error occurred");
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void onMaterialSearch(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(
                Key.key("powersmp", "order/search")
        )) {
            return;
        }

        if (!(event.getCommonConnection()
                instanceof PlayerGameConnection connection)) {
            return;
        }

        Player player = connection.getPlayer();

        DialogResponseView view = event.getDialogResponseView();
        if (view == null) {
            return;
        }

        String searchText = view.getText("search_material");

        if (searchText == null) {
            searchText = "";
        }

        searchText = searchText.trim();

        player.showDialog(
                new OrderDialog(
                        plugin,
                        manager,
                        searchText
                ).getDialog()
        );
    }

    @EventHandler
    public void onMaterialSearchClear(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(
                Key.key("powersmp", "order/clear-search")
        )) {
            return;
        }

        if (!(event.getCommonConnection()
                instanceof PlayerGameConnection connection)) {
            return;
        }

        Player player = connection.getPlayer();

        player.showDialog(
                new OrderDialog(
                        plugin,
                        manager
                ).getDialog()
        );
    }
}