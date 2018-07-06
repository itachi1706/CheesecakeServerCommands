package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 6/7/2018.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class NoteblockSongsCommand implements ICommand {

    private List<String> aliases;

    public NoteblockSongsCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("noteblocksongs");
        this.aliases.add("nbs");
    }

    @Override
    public String getName() {
        return "nbs";
    }

    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/nbs play/refresh/stop/next";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) throws CommandException {

        if(args.length == 0)
        {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: " + getUsage(iCommandSender));
            return;
        }

        String subCmd = args[0];
        switch (subCmd.toLowerCase()) {
            case "play":
                if (args.length > 1) {
                    // see if its chat, info or all
                    switch (args[1]) {
                        case "all":
                            ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + "Started playing songs with song info in both chat and hotbar");
                            NoteblockSongs.messageState = NoteblockSongs.MESSAGE_STATE_ALL;
                            break;
                        case "info":
                            ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + "Started playing songs with song info above hotbar");
                            NoteblockSongs.messageState = NoteblockSongs.MESSAGE_STATE_INFO;
                            break;
                        default:
                            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Type. Assuming default");
                        case "chat":
                            ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + "Started playing songs with song info in chat");
                            NoteblockSongs.messageState = NoteblockSongs.MESSAGE_STATE_CHAT;
                            break;
                    }
                } else
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + "Started playing songs");
                NoteblockSongs.play(iCommandSender);
                break;
            case "stop":
                NoteblockSongs.refreshSongs();
                ChatHelper.sendMessage(iCommandSender, "Stopped playing songs");
                break;
            case "refresh":
                NoteblockSongs.refreshSongs();
                ChatHelper.sendMessage(iCommandSender, ChatFormatting.GREEN + "Reloaded " + NoteblockSongs.songs.size() + " songs");
                break;
            case "next":
                NoteblockSongs.next();
                ChatHelper.sendMessage(server, TextFormatting.GREEN + "Skipping to Next Song");
                break;
            default: ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: " + getUsage(iCommandSender)); break;
        }

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, "play", "stop", "next", "refresh");
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
