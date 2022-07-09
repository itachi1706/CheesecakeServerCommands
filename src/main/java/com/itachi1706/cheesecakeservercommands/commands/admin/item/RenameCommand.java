package com.itachi1706.cheesecakeservercommands.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RenameCommand extends BaseCommand {
    public RenameCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> renameItem(context.getSource(), null))
                .then(Commands.argument("name", MessageArgument.message())
                        .executes(context -> renameItem(context.getSource(), MessageArgument.getMessage(context, "name").getString())));
    }

    private int renameItem(CommandSourceStack sender, String newName) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot rename items for CONSOLE", "Cannot rename items for " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        ItemStack item = player.getMainHandItem();
        if (item == ItemStack.EMPTY) {
            sendFailureMessage(sender, "You are not holding an item");
            return 0;
        }

        String oldName = item.getDisplayName().getString();
        if (newName == null) {
            item.setHoverName(null);
            sendSuccessMessage(sender, ChatFormatting.GOLD + "Unnamed " + ChatFormatting.LIGHT_PURPLE + oldName);
            TextUtil.sendAdminChatMessage(sender, "Unnamed " + oldName);

            return Command.SINGLE_SUCCESS;
        }

        String finalNewName = newName.trim();
        item.setHoverName(new TextComponent(finalNewName));

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Renamed " + ChatFormatting.LIGHT_PURPLE + oldName + ChatFormatting.GOLD + " to " + ChatFormatting.LIGHT_PURPLE + finalNewName);
        TextUtil.sendAdminChatMessage(sender, "Renamed " + oldName + " to " + finalNewName);

        return Command.SINGLE_SUCCESS;
    }
}
