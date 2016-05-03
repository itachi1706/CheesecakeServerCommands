package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.commons.selections.WarpPoint;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.TeleportHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class TpToCommand implements ICommand {

    private List<String> aliases;

    public TpToCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("tpto");
    }

    @Override
    public String getCommandName() {
        return "tpto";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "tpto <player>";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "CONSOLE cannot teleport");
            return;
        }

        if(astring.length == 0)
        {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Usage: /tpto <player>");
            return;
        }

        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        EntityPlayerMP sender = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (sender == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.DARK_RED + "FATAL: Player Object not found");
            return;
        }

        TeleportHelper.teleport(sender, new WarpPoint(player));

        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "Teleporting to " + player.getCommandSenderName());
        ChatHelper.sendAdminMessage(iCommandSender, "Teleported to " + player.getCommandSenderName());
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
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
