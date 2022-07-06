package com.itachi1706.cheesecakeservercommands.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RepairCommand extends BaseCommand {
    private static final String MSG_REPAIR_SUCCESS = "Repaired durability for ";

    public RepairCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> repairItem(context.getSource()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> repairOtherPeopleItem(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int repairItem(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot repair item for CONSOLE");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot repair item for " + sender.getTextName());
            return 0;
        }

        ItemStack item = player.getMainHandItem();
        if (item == ItemStack.EMPTY || !item.isDamageableItem()) {
            sendFailureMessage(sender, "You are not holding an item that can be repaired");
            return 0;
        }

        item.setDamageValue(0);

        sendSuccessMessage(sender, ChatFormatting.GOLD + MSG_REPAIR_SUCCESS + ChatFormatting.LIGHT_PURPLE + item.getDisplayName().getString());
        TextUtil.sendAdminChatMessage(sender, MSG_REPAIR_SUCCESS + item.getDisplayName().getString());
        return Command.SINGLE_SUCCESS;
    }

    private int repairOtherPeopleItem(CommandSourceStack sender, ServerPlayer player) {
        ItemStack item = player.getMainHandItem();
        if (item == ItemStack.EMPTY || !item.isDamageableItem()) {
            sendFailureMessage(sender, "You are not holding an item that can be repaired");
            return 0;
        }

        item.setDamageValue(0);

        sendSuccessMessage(sender, ChatFormatting.GOLD + MSG_REPAIR_SUCCESS + ChatFormatting.LIGHT_PURPLE +
                item.getDisplayName().getString() + ChatFormatting.GOLD + " in " + player.getName().getString() + "'s hand");
        TextUtil.sendAdminChatMessage(sender, "Helped to repair " + player.getName().getString() + "'s durability for " + item.getDisplayName().getString());
        sendMessage(player, ChatFormatting.GOLD + "Item Durability repaired");
        return Command.SINGLE_SUCCESS;
    }
}
