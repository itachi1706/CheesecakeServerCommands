package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class BurnCommand implements ICommand {

    private List<String> aliases;

    public BurnCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("burn");
    }

    @Override
    public String getName() {
        return "burn";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/burn [player] [duration]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot burn CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot burn" + iCommandSender.getName());
                    return;
                }

                player.setFire(15);
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "You were burned");
                ChatHelper.sendAdminMessage(iCommandSender, "Set own self on fire");
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        if (args.length == 1) {
            player.setFire(15);
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Burned " + player.getName());
            ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getName() + " on fire");
            ChatHelper.sendMessage(player, TextFormatting.GOLD + "You were burned");
            return;
        }

        // Burn for specified duration
        int duration = CommandBase.parseInt(args[1], 0);
        player.setFire(duration);
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Burned " + player.getName() + " for " + duration + " seconds");
        ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getName() + " on fire for " + duration + " seconds");
        ChatHelper.sendMessage(player, TextFormatting.GOLD + "You were burned");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        return Collections.emptyList();
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
