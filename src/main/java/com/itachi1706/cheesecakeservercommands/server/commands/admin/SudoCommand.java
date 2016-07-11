package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class SudoCommand implements ICommand {

    private List<String> aliases;

    public SudoCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("sudo");
    }

    @Override
    public String getCommandName() {
        return "sudo";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "sudo <player> <command>";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] astring) throws CommandException {
        if (astring.length <= 1) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Usage: /sudo <player> <command>");
            return;
        }

        String subname = astring[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Player not found");
            return;
        }

        String[] args = Arrays.copyOfRange(astring, 1, astring.length);
        String cmd = StringUtils.join(args, " ");

        ServerUtil.getServerInstance().getCommandManager().executeCommand(player, cmd);
        ChatHelper.sendMessage(iCommandSender, "Executed \"" + cmd + "\" as " + player.getName());
        ChatHelper.sendAdminMessage(iCommandSender, "Executed \"" + cmd + "\" as " + player.getName());
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
