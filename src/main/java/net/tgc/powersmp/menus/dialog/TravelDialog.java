package net.tgc.powersmp.menus.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TravelDialog extends BasicDialog {
    MiniMessage mm = MiniMessage.miniMessage();
    List<ActionButton> buttons;

    public TravelDialog(Player p) {
        buttons = new ArrayList<>();
        this.setTitle(mm.deserialize("<green>Fast Travel Menu"));

        buttons.add(new BasicActionButton()
                .setTitle(mm.deserialize("<green>Spawn"))
                .setAction(DialogAction.customClick(Key.key("powersmp", "fast_travel/spawn"), null))
                .setWidth(150)
                .getButton()
        );

        buttons.add(new BasicActionButton()
                .setTitle(mm.deserialize("<red>Secret Stronghold"))
                .setAction(DialogAction.customClick(Key.key("powersmp", "fast_travel/secret_stronghold"), null))
                .setWidth(150)
                .getButton()
        );

        buttons.add(new BasicActionButton()
                .setTitle(mm.deserialize("<dark_purple>Nether Dungeon"))
                .setAction(DialogAction.customClick(Key.key("powersmp", "fast_travel/nether_dungeon"), null))
                .setWidth(150)
                .getButton()
        );

        this.setType(DialogType.multiAction(buttons).build());
        this.setCloseableEsc(true);
    }
}
