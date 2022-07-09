package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class InvSeeCommand extends BaseCommand {
    public InvSeeCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> openOwnInventory(context.getSource()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> openOthersInventory(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int openOwnInventory(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "CONSOLE cannot use invsee. Sorry :(", "Cannot see inventory of " + sender.getTextName());
        if (player == null) {
            return 0;
        }

        viewInventory(player, player);

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Opened your inventory");
        TextUtil.sendAdminChatMessage(sender, "Seen own inventory");

        return Command.SINGLE_SUCCESS;
    }

    private int openOthersInventory(CommandSourceStack sender, ServerPlayer player) {
        ServerPlayer source = ensureIsPlayer(sender, "CONSOLE cannot use invsee. Sorry :(", "Cannot see other's inventory of " + sender.getTextName());
        if (source == null) {
            return 0;
        }

        viewInventory(source, player);

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Opened " + player.getDisplayName().getString() + "'s inventory");
        TextUtil.sendAdminChatMessage(sender, "Seen " + player.getDisplayName().getString() + "'s inventory");

        return Command.SINGLE_SUCCESS;
    }

    private void viewInventory(ServerPlayer viewer, ServerPlayer target) {
        Inventory container = target.getInventory();
        viewer.openMenu(new SimpleMenuProvider((i, inventory, player1) -> new ChestMenu(MenuType.GENERIC_9x4, i, inventory, container, 4),
                new TextComponent(target.getDisplayName().getString() + "'s Inventory")));
    }
}
