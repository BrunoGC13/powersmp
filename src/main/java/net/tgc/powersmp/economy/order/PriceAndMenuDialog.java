package net.tgc.powersmp.economy.order;

import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.menus.dialog.BasicActionButton;
import net.tgc.powersmp.menus.dialog.BasicDialog;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.UncheckedIOException;

@SuppressWarnings("UnstableApiUsage")
public class PriceAndMenuDialog extends BasicDialog {
    MiniMessage mm = MiniMessage.miniMessage();

    public PriceAndMenuDialog(Material mat, OrderManager manager) {
        this.setTitle(mm.deserialize("Select price and amount"));
        this.setCloseableEsc(true);

        BinaryTagHolder payload;

        try {
            payload = BinaryTagHolder.binaryTagHolder(
                    TagStringIO.builder().build().asString(
                            CompoundBinaryTag.builder()
                                    .putString("material", mat.name())
                                    .build()
                    )
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        this.setType(DialogType.confirmation(
                new BasicActionButton()
                        .setTitle(mm.deserialize("<green>Next"))
                        .setWidth(150)
                        .setAction(DialogAction.customClick(Key.key("powersmp", "order/finish"), payload))
                        .getButton(),
                new BasicActionButton()
                        .setTitle(mm.deserialize("<red>Exit"))
                        .setWidth(150)
                        .getButton()

        ));

        this.addBody(DialogBody.item(new ItemStack(mat))
                .showTooltip(false)
                .build());

        this.addInput(DialogInput.text("order_amount", mm.deserialize("Amount"))
                .width(150)
                .build());

        this.addInput(DialogInput.text("order_price_each", mm.deserialize("Price Each"))
                .width(150)
                .build());
    }
}
