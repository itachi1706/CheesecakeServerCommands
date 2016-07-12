package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.commons.selections.WarpPoint;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.TeleportHelper;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
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
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (!PlayerMPUtil.isPlayer(iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, "CONSOLE cannot teleport");
            return;
        }

        if(args.length == 0)
        {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Usage: /tpto <player>");
            return;
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Player not found");
            return;
        }

        EntityPlayerMP sender = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
        if (sender == null) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.DARK_RED + "FATAL: Player Object not found");
            return;
        }

        TeleportHelper.teleport(sender, new WarpPoint(player));

        ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "Teleporting to " + player.getName());
        ChatHelper.sendAdminMessage(iCommandSender, "Teleported to " + player.getName());
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, ServerUtil.getServerInstance().getAllUsernames());
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
