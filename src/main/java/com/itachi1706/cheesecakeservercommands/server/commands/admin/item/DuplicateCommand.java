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
public class DuplicateCommand implements ICommand {

    private List<String> aliases;

    public DuplicateCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("duplicate");
        this.aliases.add("more");
    }

    @Override
    public String getCommandName() {
        return "duplicate";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "duplicate [amount]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot duplicate for CONSOLE");
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot duplicate for " + iCommandSender.getCommandSenderName());
            return;
        }

        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "No items selected");
            return;
        }

        int stackSize = 0;
        if (astring.length > 0) {
            String sizeStr = astring[0];
            try {
                stackSize = Integer.parseInt(sizeStr);
            } catch (NumberFormatException e) {
                stackSize = 0;
            }
        }

        ItemStack newItem = stack.copy();
        if (stackSize > 0) {
            newItem.stackSize = stackSize;
        }

        PlayerMPUtil.give(player, newItem);

        String message = EnumChatFormatting.GOLD + "Duplicated " + stack.getDisplayName();
        String adminmessage = "Duplicated " + stack.getDisplayName();
        if (stackSize > 0) {
            message += " with " + EnumChatFormatting.AQUA + stackSize + EnumChatFormatting.GOLD + " items in the stack";
            adminmessage += " with " + stackSize + " items in the stack";
        }
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
