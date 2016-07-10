package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// TODO: Add to Main Command

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class DuplicateCommand implements ICommand {

    private List<String> aliases;

    public DuplicateCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("duplicate");
    }

    @Override
    public String getCommandName() {
        return "duplicate";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "duplicate [amount] [spillover] (Spillover if inventory gets full while duplicating)";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot duplicate for CONSOLE");
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot duplicate for " + iCommandSender.getName());
            return;
        }

        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "No items selected");
            return;
        }

        int stackSize = 0;
        if (args.length > 0) {
            stackSize = CommandBase.parseInt(iCommandSender, args[0]);
        }

        ItemStack newItem = stack.copy();
        if (stackSize > 0) {
            newItem.stackSize = stackSize;
        }

        boolean overflow = false;
        if (args.length > 1) {
            overflow = CommandBase.parseBoolean(iCommandSender, args[1]);
        }

        if (overflow)
            PlayerMPUtil.give(player, newItem);
        else
            PlayerMPUtil.giveNormal(player, newItem);

        String message = ChatFormatting.GOLD + "Duplicated " + stack.getDisplayName();
        String adminmessage = "Duplicated " + stack.getDisplayName();
        if (stackSize > 0) {
            message += " with " + ChatFormatting.AQUA + stackSize + ChatFormatting.GOLD + " items in the stack";
            adminmessage += " with " + stackSize + " items in the stack";
        }
        ChatHelper.sendMessage(iCommandSender, message);
        ChatHelper.sendAdminMessage(iCommandSender, adminmessage);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        return null;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
