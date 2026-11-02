package net.tgc.powersmp.menus.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class RulesDialog extends BasicDialog {
    private final Main plugin;
    MiniMessage mm = MiniMessage.miniMessage();

    public RulesDialog(Main plugin) {
        this.plugin = plugin;
        this.setTitle(mm.deserialize("<red>Our server rules"));
        this.setCloseableEsc(false);
        this.setType(DialogType.notice(
                new BasicActionButton()
                        .setTitle(mm.deserialize("<green>Accept our rules"))
                        .setAction(DialogAction.customClick(Key.key("powersmp:rules/accept"), null))
                        .getButton()
        ));

        this.addBody(DialogBody.item(new ItemStack(Material.BOOK))
                        .showDecorations(false)
                        .showTooltip(false)
                .build());

        List<String> rules = plugin.getConfig().getStringList("rules");

        for (String rule : rules) {
            this.addBody(DialogBody.plainMessage(mm.deserialize(rule)));
        }
    }
}
