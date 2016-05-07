package com.itachi1706.cheesecakeservercommands.server.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

// TODO: Add to Main Command

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class MoreItemsCommand implements ICommand {

    private List<String> aliases;

    public MoreItemsCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("more");
    }

    @Override
    public String getCommandName() {
        return "more";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "more";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot duplicate max stack items for CONSOLE");
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot duplicate max stack items for " + iCommandSender.getCommandSenderName());
            return;
        }

        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "No items selected");
            return;
        }

        int stackSize = stack.getMaxStackSize() - stack.stackSize;

        if (stackSize == 0) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "You already have the max stack size of the item");
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Use /duplicate to create a new stack");
            return;
        }

        ItemStack newItem = stack.copy();
        newItem.stackSize = stackSize;

        PlayerMPUtil.giveNormal(player, newItem);

        String message = EnumChatFormatting.GOLD + "Gave you more of " + stack.getDisplayName();
        String adminmessage = "Given more of " + stack.getDisplayName() + " to self";
        ChatHelper.sendMessage(iCommandSender, message);
        ChatHelper.sendAdminMessage(iCommandSender, adminmessage);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        return 0;
    }
}