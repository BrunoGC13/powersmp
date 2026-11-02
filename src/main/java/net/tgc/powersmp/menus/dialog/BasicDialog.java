package net.tgc.powersmp.menus.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class BasicDialog {
    private Component title;
    private List<DialogInput> inputs = new ArrayList<>();
    private List<DialogBody> bodyElements = new ArrayList<>();
    private DialogType type;
    private boolean closeableEsc;

    public void addInput(DialogInput input) {
        this.inputs.add(input);
    }

    public void addBody(DialogBody body) {
        this.bodyElements.add(body);
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public void setType(DialogType type) {
        this.type = type;
    }

    public void setCloseableEsc(boolean closable) {
        this.closeableEsc = closable;
    }

    public Dialog getDialog() {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(this.title)
                        .body(this.bodyElements)
                        .inputs(this.inputs)
                        .canCloseWithEscape(closeableEsc)
                        .build())
                .type(this.type)
        );
    }

    public void displayDialogTo(Player p) {
        p.showDialog(getDialog());
    }

}
