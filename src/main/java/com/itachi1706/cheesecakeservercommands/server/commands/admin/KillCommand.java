package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class KillCommand implements ICommand {

    private List<String> aliases;

    public KillCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("kill");
    }

    @Override
    public String getName() {
        return "kill";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/kill [player] [cause]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (args.length == 0) {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot kill CONSOLE");
                return;
            } else {
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot kill" + iCommandSender.getName());
                    return;
                }

                player.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "You were slain");
                ChatHelper.sendAdminMessage(iCommandSender, "Took their own life");
                return;
            }
        }

        String subname = args[0];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        // Make sure its a valid thing
        if (args.length > 1 && CheesecakeServerCommands.knownDamageSources.containsKey(args[1])) {
            player.attackEntityFrom(CheesecakeServerCommands.knownDamageSources.get(args[1]), Float.MAX_VALUE);
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Killed " + player.getName() + " with " + args[1]);
            ChatHelper.sendAdminMessage(iCommandSender, "Killed " + player.getName() + " with " + args[1]);
        } else {
            player.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "Killed " + player.getName());
            ChatHelper.sendAdminMessage(iCommandSender, "Killed " + player.getName());
        }

        ChatHelper.sendMessage(player, TextFormatting.GOLD + "You were slain");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, new ArrayList<>(CheesecakeServerCommands.knownDamageSources.keySet()));
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
