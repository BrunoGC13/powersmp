package net.tgc.powersmp.menus.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class TestDialog extends BasicDialog {
    public TestDialog() {
        MiniMessage mm = MiniMessage.miniMessage();

        this.setTitle(mm.deserialize("<red>Test <green>Dialog"));
        this.setCloseableEsc(false);
        this.setType(DialogType.notice());

        this.addBody(DialogBody.item(
                new ItemStack(Material.BOOK)
        ).build());

        this.addBody(DialogBody.plainMessage(
                mm.deserialize("<yellow>Hello <blue>there!"))
        );
    }
}
