package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class GamemodeCommand implements ICommand {

    private List<String> aliases;

    public GamemodeCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("gm");
    }

    @Override
    public String getName() {
        return "gm";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "gm <creative/adventure/survival> [player]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        if (args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /gm <creative/adventure/survival> [player]");
            return;
        }

        String togm = args[0];

        if(args.length == 1)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot set CONSOLE's Game Mode");
                return;
            } else {
                // Gamemode yourself
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot set " + iCommandSender.getName() + "'s Game Mode");
                    return;
                }
                GameType gamemode = getGameMode(togm);
                if (gamemode.equals(GameType.NOT_SET)) {
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Unknown Game Mode.");
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Available modes: creative, adventure, survival");
                    return;
                }
                player.setGameType(gamemode);
                ChatHelper.sendMessage(iCommandSender, "Set own gamemode to " + TextFormatting.GOLD + WordUtils.capitalize(gamemode.getName()));
                ChatHelper.sendAdminMessage(iCommandSender, "Set own gamemode to " + WordUtils.capitalize(gamemode.getName()));
                return;
            }
        }

        // Gamemode others
        String subname = args[1];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player not found");
            return;
        }

        GameType gamemode = getGameMode(togm);
        if (gamemode.equals(GameType.NOT_SET)) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Unknown Game Mode.");
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Available modes: creative, adventure, survival");
            return;
        }
        player.setGameType(GameType.SURVIVAL);
        ChatHelper.sendMessage(iCommandSender, "Set " + player.getName() + " gamemode to " + TextFormatting.GOLD + WordUtils.capitalize(gamemode.getName()));
        ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getName() + " gamemode to " + WordUtils.capitalize(gamemode.getName()));
    }

    private GameType getGameMode(String gamemode) {
        if (gamemode.equalsIgnoreCase("survival") || gamemode.equalsIgnoreCase("s") || gamemode.equals("0")) {
            return GameType.SURVIVAL;
        } else if (gamemode.equalsIgnoreCase("adventure") || gamemode.equalsIgnoreCase("a") || gamemode.equals("2")) {
            return GameType.ADVENTURE;
        } else if (gamemode.equalsIgnoreCase("creative") || gamemode.equalsIgnoreCase("c") || gamemode.equals("1")) {
            return GameType.CREATIVE;
        } else if (gamemode.equalsIgnoreCase("spectator") || gamemode.equalsIgnoreCase("sp") || gamemode.equals("3")) {
            return GameType.SPECTATOR;
        }else {
            return GameType.NOT_SET;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "creative", "survival", "adventure");
        }

        boolean showSecond = false;
        if (args[0].equalsIgnoreCase("creative")) showSecond = true;
        if (args[0].equalsIgnoreCase("survival")) showSecond = true;
        if (args[0].equalsIgnoreCase("adventure")) showSecond = true;
        if (args[0].equalsIgnoreCase("s")) showSecond = true;
        if (args[0].equalsIgnoreCase("c")) showSecond = true;
        if (args[0].equalsIgnoreCase("a")) showSecond = true;
        if (args[0].equalsIgnoreCase("0")) showSecond = true;
        if (args[0].equalsIgnoreCase("1")) showSecond = true;
        if (args[0].equalsIgnoreCase("2")) showSecond = true;

        if (args.length == 2 && showSecond)
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
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
