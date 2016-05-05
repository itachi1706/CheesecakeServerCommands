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
public class RenameCommand implements ICommand {

    private List<String> aliases;

    public RenameCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("renameitem");
    }

    @Override
    public String getCommandName() {
        return "renameitem";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "renameitem <newname>";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (astring.length == 0) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Usage: /renameitem <newname>");
            return;
        }

        String newname = astring[0];

        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "Cannot rename item for CONSOLE");
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, "Cannot rename item " + iCommandSender.getCommandSenderName());
            return;
        }

        ItemStack item = player.getCurrentEquippedItem();
        if (item == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "You are not holding an item");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (String s : astring) {
            builder.append(s + " ");
        }

        String oldname = item.getDisplayName();
        String finalnewname = builder.toString().trim();
        item.setStackDisplayName(finalnewname);

        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Renamed " + oldname + " to " + finalnewname);
        ChatHelper.sendAdminMessage(iCommandSender, "Renamed " + oldname + " to " + finalnewname);
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