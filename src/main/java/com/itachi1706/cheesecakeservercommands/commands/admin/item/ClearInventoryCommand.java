package com.itachi1706.cheesecakeservercommands.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ClearInventoryCommand extends BaseCommand {
    private static final String MSG_INV_CLEAR_PLAYER = "Inventory Cleared of ";
    private static final String MSG_INV_CLEAR_ADMIN = "Cleared Inventory of ";
    private static final String MSG_ITEMS = " items";
    private static final String MSG_ITEM = " of a specified item";

    private static final String ARG_PLAYER = "players";
    private static final String ARG_ITEM_PREDICATE = "itemPredicate";

    public ClearInventoryCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> clearOwnInventory(context.getSource()))
                .then(Commands.argument(ARG_PLAYER, EntityArgument.player())
                        .executes(context -> clearOthersInventory(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER)))
                        .then(Commands.argument(ARG_ITEM_PREDICATE, ItemPredicateArgument.itemPredicate())
                                .executes(context -> clearSpecificItemFromPlayerInventory(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER),
                                        ItemPredicateArgument.getItemPredicate(context, ARG_ITEM_PREDICATE), -1))
                                .then(Commands.argument("count", IntegerArgumentType.integer(0))
                                        .executes(context -> clearSpecificItemFromPlayerInventory(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER),
                                                ItemPredicateArgument.getItemPredicate(context, ARG_ITEM_PREDICATE), IntegerArgumentType.getInteger(context, "count"))))));
    }

    private int clearOwnInventory(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot clear inventory of CONSOLE");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot clear inventory of " + sender.getTextName());
            return 0;
        }

        int clearedCount = clearInventory(player, item -> true, -1);
        sendSuccessMessage(sender, ChatFormatting.GOLD + MSG_INV_CLEAR_PLAYER + clearedCount + MSG_ITEMS);
        TextUtil.sendAdminChatMessage(sender, "Cleared own inventory of " + clearedCount + MSG_ITEMS);

        return Command.SINGLE_SUCCESS;
    }

    private int clearOthersInventory(CommandSourceStack sender, ServerPlayer player) {
        int clearedCount = clearInventory(player, item -> true, -1);
        sendSuccessMessage(sender, ChatFormatting.GOLD + MSG_INV_CLEAR_ADMIN + player.getName().getString() + " of " + clearedCount + MSG_ITEMS);
        TextUtil.sendAdminChatMessage(sender, MSG_INV_CLEAR_ADMIN + player.getName().getString() + " of " + clearedCount + MSG_ITEMS);
        sendMessage(player, ChatFormatting.GOLD + MSG_INV_CLEAR_PLAYER + clearedCount + MSG_ITEMS);

        return Command.SINGLE_SUCCESS;
    }

    private int clearSpecificItemFromPlayerInventory(CommandSourceStack sender, ServerPlayer player, Predicate<ItemStack> item, int count) {
        int clearedCount = clearInventory(player, item, count);

        if (count == 0) {
            // We are not removing anything, just counting
            sendSuccessMessage(sender, ChatFormatting.GOLD + "Found " + clearedCount + " items on " + player.getName().getString());
            TextUtil.sendAdminChatMessage(sender, "Found out that " + player.getName().getString() + " has " + clearedCount + " of an item");
        } else {
            sendSuccessMessage(sender, ChatFormatting.GOLD + MSG_INV_CLEAR_ADMIN + player.getName().getString() + " of " + clearedCount + MSG_ITEM);
            TextUtil.sendAdminChatMessage(sender, MSG_INV_CLEAR_ADMIN + player.getName().getString() + " of " + clearedCount + MSG_ITEM);
            sendMessage(player, ChatFormatting.GOLD + MSG_INV_CLEAR_PLAYER + clearedCount + MSG_ITEM);
        }

        return Command.SINGLE_SUCCESS;
    }

    private int clearInventory(ServerPlayer player, Predicate<ItemStack> item, int count) {
        int clearedCount = player.getInventory().clearOrCountMatchingItems(item, count, player.inventoryMenu.getCraftSlots());
        player.containerMenu.broadcastChanges();
        player.inventoryMenu.slotsChanged(player.getInventory());

        return clearedCount;
    }
}
