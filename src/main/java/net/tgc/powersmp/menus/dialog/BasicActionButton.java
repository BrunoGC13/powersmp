package net.tgc.powersmp.menus.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.Component;

@SuppressWarnings("UnstableApiUsage")
public class BasicActionButton {
    private Component title;
    private Component tooltip;
    private int width = 100;
    private DialogAction action;

    public BasicActionButton setTitle(Component title) {
        this.title = title;
        return this;
    }

    public BasicActionButton setTooltip(Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public BasicActionButton setWidth(int width) {
        this.width = width;
        return this;
    }

    public BasicActionButton setAction(DialogAction action) {
        this.action = action;
        return this;
    }

    public ActionButton getButton() {
        return ActionButton.create(
                this.title, this.tooltip, this.width, this.action
        );
    }
}
