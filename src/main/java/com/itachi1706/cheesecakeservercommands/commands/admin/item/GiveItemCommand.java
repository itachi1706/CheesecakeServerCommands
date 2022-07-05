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
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GiveItemCommand extends BaseCommand {
    private static final String FORMAT_RECEIVE_ITEM = "%sReceived %s%d%s of %s%s";

    private static final String ARG_ITEM = "item";
    private static final String ARG_COUNT = "count";
    private static final String ARG_DAMAGE = "damage";
    private static final String ARG_PLAYER = "player";

    public GiveItemCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.argument(ARG_ITEM, ItemArgument.item())
                        .executes(context -> giveSelf(context.getSource(), ItemArgument.getItem(context, ARG_ITEM).getItem(), 1, -1))
                        .then(Commands.argument(ARG_COUNT, IntegerArgumentType.integer(1))
                                .executes(context -> giveSelf(context.getSource(), ItemArgument.getItem(context, ARG_ITEM).getItem(), IntegerArgumentType.getInteger(context, ARG_COUNT), -1))
                                .then(Commands.argument(ARG_DAMAGE, IntegerArgumentType.integer(0))
                                        .executes(context -> giveSelf(context.getSource(), ItemArgument.getItem(context, ARG_ITEM).getItem(), IntegerArgumentType.getInteger(context, ARG_COUNT),
                                                IntegerArgumentType.getInteger(context, ARG_DAMAGE)))
                                        .then(Commands.argument(ARG_PLAYER, EntityArgument.player())
                                                .executes(context -> givePlayer(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER), ItemArgument.getItem(context, ARG_ITEM).getItem(),
                                                        IntegerArgumentType.getInteger(context, ARG_COUNT), IntegerArgumentType.getInteger(context, ARG_DAMAGE), false))
                                                .then(Commands.argument("spill", BoolArgumentType.bool()).executes(context -> givePlayer(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER),
                                                        ItemArgument.getItem(context, ARG_ITEM).getItem(), IntegerArgumentType.getInteger(context, ARG_COUNT),
                                                        IntegerArgumentType.getInteger(context, ARG_DAMAGE), BoolArgumentType.getBool(context, "spill"))))))));
    }

    private int giveSelf(CommandSourceStack sender, Item item, int qty, int damage) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot give items to CONSOLE");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot give items to " + sender.getTextName());
            return 0;
        }

        ItemStack stack = getItemStack(item, qty, damage);
        giveItem(player, stack, false);

        sendSuccessMessage(sender, ChatFormatting.GOLD +  "Received " + ChatFormatting.AQUA + qty + ChatFormatting.GOLD
                + " of " + ChatFormatting.LIGHT_PURPLE + stack.getDisplayName().getString());
        TextUtil.sendAdminChatMessage(sender, String.format(FORMAT_RECEIVE_ITEM, ChatFormatting.GOLD, ChatFormatting.AQUA, qty, ChatFormatting.GOLD, ChatFormatting.LIGHT_PURPLE, stack.getDisplayName().getString()));

        return Command.SINGLE_SUCCESS;
    }

    private int givePlayer(CommandSourceStack sender, ServerPlayer player, Item item, int qty, int damage, boolean spillOver) {
        ItemStack stack = getItemStack(item, qty, damage);
        giveItem(player, stack, spillOver);

        sendSuccessMessage(sender, ChatFormatting.GOLD +  "Gave " + ChatFormatting.AQUA + qty + ChatFormatting.GOLD
                + " of " + ChatFormatting.LIGHT_PURPLE + stack.getDisplayName().getString() + ChatFormatting.GOLD + " to " + player.getName().getString());
        TextUtil.sendAdminChatMessage(sender, "Gave " + qty + " of " + stack.getDisplayName().getString() + " to " + player.getName().getString());
        sendMessage(player, String.format(FORMAT_RECEIVE_ITEM, ChatFormatting.GOLD, ChatFormatting.AQUA, qty, ChatFormatting.GOLD, ChatFormatting.LIGHT_PURPLE, stack.getDisplayName().getString()));

        return Command.SINGLE_SUCCESS;
    }

    private ItemStack getItemStack(Item item, int qty, int damage) {
        ItemStack stack = new ItemStack(item, qty);
        if (damage != -1) {
            stack.setDamageValue(damage);
        }

        return stack;
    }

    private void giveItem(ServerPlayer player, ItemStack item, boolean spillOver) {
        if (!spillOver) ServerPlayerUtil.giveNormal(player, item);
        else ServerPlayerUtil.give(player, item);
    }
}
