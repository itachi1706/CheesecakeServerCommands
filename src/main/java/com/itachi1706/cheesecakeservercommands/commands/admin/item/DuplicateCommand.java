package com.itachi1706.cheesecakeservercommands.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class DuplicateCommand extends BaseCommand {
    private static final String ARG_AMOUNT = "amount";
    public DuplicateCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> duplicateItem(context.getSource(), 0, false))
                .then(Commands.argument(ARG_AMOUNT, IntegerArgumentType.integer(1))
                        .executes(context -> duplicateItem(context.getSource(), IntegerArgumentType.getInteger(context, ARG_AMOUNT), false))
                        .then(Commands.argument("spillover", BoolArgumentType.bool())
                                .executes(context -> duplicateItem(context.getSource(), IntegerArgumentType.getInteger(context, ARG_AMOUNT), BoolArgumentType.getBool(context, "spillover")))));

    }

    private int duplicateItem(CommandSourceStack sender, int amount, boolean spillover) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot duplicate for CONSOLE", "Cannot duplicate for " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        ItemStack stack = player.getMainHandItem();
        if (stack == ItemStack.EMPTY) {
            sendFailureMessage(sender, "No items selected");
            return 0;
        }

        int stackSize = Math.max(amount, 0);

        ItemStack newItem = stack.copy();
        if (stackSize > 0) {
            newItem.setCount(stackSize);
        }

        if (spillover) {
            ServerPlayerUtil.give(player, newItem);
        } else {
            ServerPlayerUtil.giveNormal(player, newItem);
        }

        String message = ChatFormatting.GOLD + "Duplicated " + ChatFormatting.LIGHT_PURPLE +  stack.getDisplayName().getString() + ChatFormatting.GOLD;
        String adminmessage = "Duplicated " + stack.getDisplayName().getString();
        if (stackSize > 0) {
            message += " with " + ChatFormatting.AQUA + stackSize + ChatFormatting.GOLD + " items in the stack";
            adminmessage += " with " + stackSize + " items in the stack";
        }

        sendSuccessMessage(sender, message);
        TextUtil.sendAdminChatMessage(sender, adminmessage);

        return Command.SINGLE_SUCCESS;
    }
}
