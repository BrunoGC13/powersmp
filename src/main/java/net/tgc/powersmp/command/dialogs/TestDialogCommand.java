package net.tgc.powersmp.command.dialogs;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.tgc.powersmp.menus.dialog.TestDialog;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TestDialogCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        TestDialog testDialog = new TestDialog();
        testDialog.displayDialogTo((Player) Objects.requireNonNull(commandSourceStack.getExecutor()));
    }
}
