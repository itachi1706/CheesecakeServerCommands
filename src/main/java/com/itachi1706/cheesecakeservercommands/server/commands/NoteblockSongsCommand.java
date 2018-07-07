package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
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
    @Nonnull
    public String getName() {
        return "nbs";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/nbs help/play/refresh/stop/next/randomplay/list/nowplaying";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    private void displayHelp(ICommandSender sender) {
        ChatHelper.sendMessage(sender, TextFormatting.DARK_GREEN + "--- Commands for Noteblock Songs ---");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/nbs help" + TextFormatting.WHITE + " Displays this help page");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/nbs nowplaying" + TextFormatting.WHITE + " View currently playing song");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/nbs play [all/chat/info] [index]" + TextFormatting.WHITE + " Starts server-wide song playing");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/nbs refresh" + TextFormatting.WHITE + " Refreshes song list");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/nbs stop" + TextFormatting.WHITE + " Stops server-wide song playing");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/nbs next" + TextFormatting.WHITE + " Skips to the next song");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/nbs list [page]" + TextFormatting.WHITE + " List all songs on the server");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/nbs randomplay" + TextFormatting.WHITE + " Starts playing server-wide songs in random order");
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
            case "help": displayHelp(iCommandSender); break;
            // TODO: List command that list all songs with an index, paginated by pages of 10 songs
            // TODO: Random play command that is just toggling a random boolean
            case "play":
                if (NoteblockSongs.isPlaying()) {
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Songs are already being played server-wide");
                    return;
                }
                if (args.length > 1) {
                    // see if its chat, info or all
                    switch (args[1].toLowerCase()) {
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
                if (args.length > 2) {
                    try {
                        int index = Integer.parseInt(args[2]);
                        NoteblockSongs.play(iCommandSender, index - 1);
                        return;
                    } catch (NumberFormatException ignored) {}
                }
                NoteblockSongs.play(iCommandSender);
                break;
            case "stop":
                if (!NoteblockSongs.isPlaying()) {
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Songs are already not being played server-wide");
                    return;
                }
                NoteblockSongs.refreshSongs();
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + "Stopped playing songs");
                break;
            case "refresh":
                NoteblockSongs.refreshSongs();
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + "Reloaded " + NoteblockSongs.songs.size() + " songs");
                break;
            case "next":
                NoteblockSongs.next();
                ChatHelper.sendMessage(server, TextFormatting.GREEN + "Skipping to Next Song");
                break;
            case "nowplaying":
                NoteblockSongs.sendMsg(iCommandSender);
                break;
            default: ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: " + getUsage(iCommandSender)); break;
        }

    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos pos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, "help", "list", "next", "nowplaying", "play", "randomplay", "refresh", "stop");
        if (args.length == 2 && (args[0].equalsIgnoreCase("play") || args[0].equalsIgnoreCase("randomplay")))
            return CommandBase.getListOfStringsMatchingLastWord(args, "all", "chat", "info");
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
