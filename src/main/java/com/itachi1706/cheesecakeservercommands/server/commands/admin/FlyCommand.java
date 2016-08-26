package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.WorldUtil;
import net.minecraft.util.text.TextFormatting;
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
public class FlyCommand implements ICommand {

    private List<String> aliases;

    public FlyCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("fly");
    }

    @Override
    public String getCommandName() {
        return "fly";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "fly [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        if(args.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot set flight status of CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot set " + iCommandSender.getName() + "'s flight status");
                    return;
                }
                player.capabilities.allowFlying = !player.capabilities.allowFlying;
                if (!player.onGround)
                    player.capabilities.isFlying = player.capabilities.allowFlying;
                if (!player.capabilities.allowFlying)
                    WorldUtil.placeInWorld(player);
                player.sendPlayerAbilities();
                ChatHelper.sendMessage(iCommandSender, "Flight Mode " + (player.capabilities.allowFlying ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));
                ChatHelper.sendAdminMessage(iCommandSender, "Set own flight mode to " + (player.capabilities.allowFlying ? "Enabled" : "Disabled"));
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        player.capabilities.allowFlying = !player.capabilities.allowFlying;
        if (!player.onGround)
            player.capabilities.isFlying = player.capabilities.allowFlying;
        if (!player.capabilities.allowFlying)
            WorldUtil.placeInWorld(player);
        player.sendPlayerAbilities();
        ChatHelper.sendMessage(iCommandSender, "Set " + player.getName() + " flight mode to " + (player.capabilities.allowFlying ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));
        ChatHelper.sendMessage(player, "Flight Mode has been set to " + (player.capabilities.allowFlying ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));
        ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getName() + " flight mode to " + (player.capabilities.allowFlying ? "Enabled" : "Disabled"));
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
