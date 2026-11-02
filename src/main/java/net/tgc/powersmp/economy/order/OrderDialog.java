package net.tgc.powersmp.economy.order;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.menus.dialog.BasicActionButton;
import net.tgc.powersmp.menus.dialog.BasicDialog;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class OrderDialog extends BasicDialog {

    private static final Key SEARCH_KEY =
            Key.key("powersmp", "order/search");

    private final MiniMessage mm = MiniMessage.miniMessage();

    private final OrderManager manager;
    private final String searchText;

    private final List<Material> allMaterials = Arrays.stream(Material.values())
            .filter(material ->
                    !material.isLegacy()
                            && material.isItem()
                            && !material.isAir()
            )
            .sorted()
            .collect(Collectors.toList());

    public OrderDialog(
            net.tgc.powersmp.Main plugin,
            OrderManager manager
    ) {
        this(plugin, manager, "");
    }

    public OrderDialog(
            net.tgc.powersmp.Main plugin,
            OrderManager manager,
            String searchText
    ) {
        this.manager = manager;
        this.searchText = searchText == null
                ? ""
                : searchText.trim();

        this.setTitle(mm.deserialize(
                this.searchText.isBlank()
                        ? "Create an order"
                        : "Create an order - " + this.searchText
        ));

        this.addInput(
                DialogInput.text(
                                "search_material",
                                mm.deserialize("<gray>Search materials...")
                        )
                        .width(200)
                        .build()
        );

        this.setType(
                DialogType.multiAction(
                        getMaterialButtons()
                ).build()
        );

        this.setCloseableEsc(true);
    }

    private List<ActionButton> getMaterialButtons() {
        List<ActionButton> actionButtons = new ArrayList<>();

        actionButtons.add(
                new BasicActionButton()
                        .setTitle(mm.deserialize("<yellow>Search"))
                        .setAction(DialogAction.customClick(SEARCH_KEY, null))
                        .setWidth(200)
                        .getButton()
        );

        if (!searchText.isBlank()) {
            actionButtons.add(
                    new BasicActionButton()
                            .setTitle(mm.deserialize("<red>Clear Search"))
                            .setAction(
                                    DialogAction.customClick(
                                            Key.key("powersmp", "order/clear-search"),
                                            null
                                    )
                            )
                            .setWidth(200)
                            .getButton()
            );
        }

        String normalizedSearch = searchText
                .toLowerCase(Locale.ROOT);

        for (Material material : allMaterials) {
            ItemStack item = new ItemStack(material);

            if (!normalizedSearch.isBlank()
                    && !matchesSearch(material, item, normalizedSearch)) {
                continue;
            }

            actionButtons.add(createMaterialButton(material, item));
        }

        if (actionButtons.size() == 1) {
            actionButtons.add(
                    new BasicActionButton()
                            .setTitle(mm.deserialize("<red>No materials found"))
                            .setWidth(200)
                            .getButton()
            );
        }

        return actionButtons;
    }

    private boolean matchesSearch(
            Material material,
            ItemStack item,
            String normalizedSearch
    ) {
        String materialName = material.name()
                .toLowerCase(Locale.ROOT)
                .replace('_', ' ');

        String displayName = PlainTextComponentSerializer.plainText()
                .serialize(item.displayName())
                .toLowerCase(Locale.ROOT);

        return materialName.contains(normalizedSearch)
                || displayName.contains(normalizedSearch);
    }

    private ActionButton createMaterialButton(
            Material material,
            ItemStack item
    ) {
        BinaryTagHolder payload;

        try {
            payload = BinaryTagHolder.binaryTagHolder(
                    TagStringIO.builder().build().asString(
                            CompoundBinaryTag.builder()
                                    .putString("material", material.name())
                                    .build()
                    )
            );
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        return new BasicActionButton()
                .setTitle(item.displayName())
                .setAction(
                        DialogAction.customClick(
                                Key.key("powersmp", "order/select/material"),
                                payload
                        )
                )
                .setWidth(200)
                .getButton();
    }
}