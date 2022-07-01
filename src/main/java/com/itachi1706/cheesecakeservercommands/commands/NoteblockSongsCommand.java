package com.itachi1706.cheesecakeservercommands.commands;

import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

public class NoteblockSongsCommand extends BaseCommand {

    public NoteblockSongsCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.literal("help").executes(context -> displayHelp(context.getSource())))
                .then(Commands.literal("list").executes(context -> displaySongList(context.getSource(), 1))
                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(context -> displaySongList(context.getSource(), IntegerArgumentType.getInteger(context, "page")))))
                .then(Commands.literal("randomplay").executes(context -> playSong(context.getSource(), true, true))
                        .then(Commands.argument("type", StringArgumentType.string()).suggests(((context, builder) -> SharedSuggestionProvider.suggest(playSuggestionList, builder)))
                                .executes(context -> playSong(context.getSource(), true, StringArgumentType.getString(context, "type")))
                                .then(Commands.argument("index", IntegerArgumentType.integer(1))
                                        .executes(context -> playSong(context.getSource(), true, StringArgumentType.getString(context, "type"), IntegerArgumentType.getInteger(context, "index")))))
                )
                .then(Commands.literal("play").executes(context -> playSong(context.getSource(), false, true))
                        .then(Commands.argument("type", StringArgumentType.string()).suggests(((context, builder) -> SharedSuggestionProvider.suggest(playSuggestionList, builder)))
                                .executes(context -> playSong(context.getSource(), false, StringArgumentType.getString(context, "type")))
                                .then(Commands.argument("index", IntegerArgumentType.integer(1))
                                        .executes(context -> playSong(context.getSource(), false, StringArgumentType.getString(context, "type"), IntegerArgumentType.getInteger(context, "index"))))))
                .then(Commands.literal("stop").executes(context -> stopPlaying(context.getSource())))
                .then(Commands.literal("refresh").executes(context -> refreshSongs(context.getSource())))
                .then(Commands.literal("next").executes(context -> nextSong(context.getSource())))
                .then(Commands.literal("nowplaying").executes(context -> getNowPlaying(context.getSource())));
    }

    private final String[] playSuggestionList = {"all", "chat", "info"};

    private int displayHelp(CommandSourceStack sender) {
        sendSuccessMessage(sender, ChatFormatting.DARK_GREEN + "--- Commands for Noteblock Songs ---");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/midi help" + ChatFormatting.WHITE + " Displays this help page");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/midi nowplaying" + ChatFormatting.WHITE + " View currently playing song");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/midi play [all/chat/info] [index]" + ChatFormatting.WHITE + " Starts server-wide song playing");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/midi refresh" + ChatFormatting.WHITE + " Refreshes song list");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/midi stop" + ChatFormatting.WHITE + " Stops server-wide song playing");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/midi next" + ChatFormatting.WHITE + " Skips to the next song");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/midi list [page]" + ChatFormatting.WHITE + " List all songs on the server");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/midi randomplay [all/chat/info]" + ChatFormatting.WHITE + " Starts playing server-wide songs in random order");
        sendSuccessMessage(sender, ChatFormatting.GREEN + "Tip: You can replace /midi with /nbs or /noteblocksongs if needed (due to mod clash etc.)");
        
        return Command.SINGLE_SUCCESS;
    }

    private int playSong(CommandSourceStack sender, boolean random, boolean defaultState) {
        if (NoteblockSongs.isPlaying()) {
            sendFailureMessage(sender, ChatFormatting.RED + "Songs are already being played server-wide");
            return 0;
        }
        if (defaultState) NoteblockSongs.messageState = NoteblockSongs.MESSAGE_STATE_CHAT; // Reset default

        NoteblockSongs.random = random;

        if (defaultState) sendSuccessMessage(sender, ChatFormatting.GREEN + "Started playing songs");
        NoteblockSongs.play(sender);

        return Command.SINGLE_SUCCESS;
    }

    private void setBaseOnType(CommandSourceStack sender, String type) {
        // see if its chat, info or all
        switch (type.toLowerCase()) {
            case "all":
                sendSuccessMessage(sender, ChatFormatting.GREEN + "Started playing songs with song info in both chat and hotbar");
                NoteblockSongs.messageState = NoteblockSongs.MESSAGE_STATE_ALL;
                break;
            case "info":
                sendSuccessMessage(sender, ChatFormatting.GREEN + "Started playing songs with song info above hotbar");
                NoteblockSongs.messageState = NoteblockSongs.MESSAGE_STATE_INFO;
                break;
            default:
                sendSuccessMessage(sender, ChatFormatting.RED + "Invalid Type. Assuming default");
            case "chat":
                sendSuccessMessage(sender, ChatFormatting.GREEN + "Started playing songs with song info in chat");
                NoteblockSongs.messageState = NoteblockSongs.MESSAGE_STATE_CHAT;
                break;
        }
    }

    private int playSong(CommandSourceStack sender, boolean random, String type) {
        setBaseOnType(sender, type);

        return playSong(sender, random, false);
    }

    private int playSong(CommandSourceStack sender, boolean random, String type, int index) {
        // see if its chat, info or all
        NoteblockSongs.random = random;
        setBaseOnType(sender, type);
        NoteblockSongs.play(sender, index - 1);
        return Command.SINGLE_SUCCESS;
    }

    private int stopPlaying(CommandSourceStack sender) {
        if (!NoteblockSongs.isPlaying()) {
            sendSuccessMessage(sender, ChatFormatting.RED + "Songs are already not being played server-wide");
            return 0;
        }
        NoteblockSongs.refreshSongs();
        sendSuccessMessage(sender, ChatFormatting.GREEN + "Stopped playing songs");

        return Command.SINGLE_SUCCESS;
    }

    private int refreshSongs(CommandSourceStack sender) {
        NoteblockSongs.refreshSongs();
        sendSuccessMessage(sender, ChatFormatting.GREEN + "Reloaded " + NoteblockSongs.songs.size() + " songs");

        return Command.SINGLE_SUCCESS;
    }

    private int nextSong(CommandSourceStack sender) {
        NoteblockSongs.next();
        sendSuccessMessage(sender, ChatFormatting.GREEN + "Skipping to Next Song");

        return Command.SINGLE_SUCCESS;
    }

    private int getNowPlaying(CommandSourceStack sender) {
        NoteblockSongs.sendMsg(sender);

        return Command.SINGLE_SUCCESS;
    }

    private int displaySongList(CommandSourceStack sender, int pageIndex) {
        int totalPages = NoteblockSongs.names.size() / 10;
        if (NoteblockSongs.names.size() % 10 != 0) totalPages++;
        if (pageIndex < 1) pageIndex = 1;
        if (pageIndex > totalPages) pageIndex = totalPages;
        sendSuccessMessage(sender, ChatFormatting.DARK_GREEN + "--- Noteblock Song List Page " + pageIndex + " of " + totalPages + " ---");
        int skipVal = ((pageIndex - 1) * 10) - 1; //0-9, 10-19, 20-29
        int toAdd = skipVal + 2;
        int printed = 0;
        for (String name : NoteblockSongs.names) {
            if (skipVal >= 0) {
                skipVal--;
                continue;
            }
            if (printed >= 10) break;
            sendSuccessMessage(sender, (toAdd + printed) + ". " + name);
            printed++;
        }

        return Command.SINGLE_SUCCESS;
    }
}
