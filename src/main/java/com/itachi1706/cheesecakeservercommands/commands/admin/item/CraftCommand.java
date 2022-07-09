package com.itachi1706.cheesecakeservercommands.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.menu.NoInterruptCraftingMenu;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class CraftCommand extends BaseCommand {
    private static final String MSG_OPEN_CRAFT = "Opened Crafting Window";

    public CraftCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> openMyCraftingWindow(context.getSource()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> openPlayerCraftingWindow(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int openMyCraftingWindow(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot open the crafting window of CONSOLE", "Cannot open the crafting window of " + sender.getTextName());
        if (player == null) {
            return 0;
        }

        openCraftingMenu(player);
        sendSuccessMessage(sender, ChatFormatting.GOLD + MSG_OPEN_CRAFT);
        TextUtil.sendAdminChatMessage(sender, MSG_OPEN_CRAFT);

        return Command.SINGLE_SUCCESS;
    }

    private int openPlayerCraftingWindow(CommandSourceStack sender, ServerPlayer player) {
        openCraftingMenu(player);

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Opened Crafting Window for " + player.getDisplayName().getString());
        TextUtil.sendAdminChatMessage(sender, "Opened Crafting Window for " + player.getDisplayName().getString());
        sendMessage(player, ChatFormatting.GOLD + MSG_OPEN_CRAFT);

        return Command.SINGLE_SUCCESS;
    }

    private void openCraftingMenu(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider((i, inventory, player1) ->
                new NoInterruptCraftingMenu(i, inventory, ContainerLevelAccess.create(player.level, new BlockPos(player.getOnPos()))), new TextComponent("Crafting")));
    }
}
