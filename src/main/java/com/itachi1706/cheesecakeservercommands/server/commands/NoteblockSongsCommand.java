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
        this.aliases.add("midi");
        this.aliases.add("nbs");
    }

    @Override
    @Nonnull
    public String getName() {
        return "noteblocksongs";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/midi help/play/refresh/stop/next/randomplay/list/nowplaying";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    private void displayHelp(ICommandSender sender) {
        ChatHelper.sendMessage(sender, TextFormatting.DARK_GREEN + "--- Commands for Noteblock Songs ---");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/midi help" + TextFormatting.WHITE + " Displays this help page");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/midi nowplaying" + TextFormatting.WHITE + " View currently playing song");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/midi play [all/chat/info] [index]" + TextFormatting.WHITE + " Starts server-wide song playing");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/midi refresh" + TextFormatting.WHITE + " Refreshes song list");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/midi stop" + TextFormatting.WHITE + " Stops server-wide song playing");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/midi next" + TextFormatting.WHITE + " Skips to the next song");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/midi list [page]" + TextFormatting.WHITE + " List all songs on the server");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "/midi randomplay [all/chat/info]" + TextFormatting.WHITE + " Starts playing server-wide songs in random order");
        ChatHelper.sendMessage(sender, TextFormatting.GREEN + "Tip: You can replace /midi with /nbs or /noteblocksongs if needed (due to mod clash etc.)");
    }

    private void displaySongList(ICommandSender sender, int pageIndex) {
        int totalPages = NoteblockSongs.names.size() / 10;
        if (NoteblockSongs.names.size() % 10 != 0) totalPages++;
        if (pageIndex < 1) pageIndex = 1;
        if (pageIndex > totalPages) pageIndex = totalPages;
        ChatHelper.sendMessage(sender, TextFormatting.DARK_GREEN + "--- Noteblock Song List Page " + pageIndex + " of " + totalPages + " ---");
        int skipVal = ((pageIndex - 1) * 10) - 1; //0-9, 10-19, 20-29
        int toAdd = skipVal + 2;
        int printed = 0;
        for (String name : NoteblockSongs.names) {
            if (skipVal >= 0) {
                skipVal--;
                continue;
            }
            if (printed >= 10) break;
            ChatHelper.sendMessage(sender, (toAdd + printed) + ". " + name);
            printed++;
        }
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
            case "list":
                if (args.length > 1) {
                    int index = 0;
                    try {
                        index = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {}
                    displaySongList(iCommandSender, index);
                    return;
                }
                displaySongList(iCommandSender, 1);
                break;
            case "randomplay": NoteblockSongs.random = true;
            case "play":
                if (NoteblockSongs.isPlaying()) {
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Songs are already being played server-wide");
                    return;
                }
                NoteblockSongs.messageState = NoteblockSongs.MESSAGE_STATE_CHAT; // Reset default
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
                } else ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + "Started playing songs");
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
            case "nowplaying": NoteblockSongs.sendMsg(iCommandSender); break;
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
