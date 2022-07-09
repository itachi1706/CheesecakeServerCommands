package com.itachi1706.cheesecakeservercommands.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class MoreItemsCommand extends BaseCommand {
    public MoreItemsCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> moreItems(context.getSource()));
    }

    private int moreItems(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot duplicate max stack items for CONSOLE", "Cannot duplicate max stacks items for " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        ItemStack stack = player.getMainHandItem();
        if (stack == ItemStack.EMPTY) {
            sendFailureMessage(sender, "No items selected");
            return 0;
        }

        int stackSize = stack.getMaxStackSize() - stack.getCount();

        if (stackSize == 0) {
            sendFailureMessage(sender, "You already have the max stack size of the item");
            sendFailureMessage(sender, "Use /duplicate to create a new stack");
            return 0;
        }

        ItemStack newItem = stack.copy();
        newItem.setCount(stackSize);

        ServerPlayerUtil.giveNormal(player, newItem);

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Gave you more of " + ChatFormatting.LIGHT_PURPLE + stack.getDisplayName().getString());
        TextUtil.sendAdminChatMessage(sender, "Given more of " + stack.getDisplayName().getString() + " to self");

        return Command.SINGLE_SUCCESS;
    }
}
