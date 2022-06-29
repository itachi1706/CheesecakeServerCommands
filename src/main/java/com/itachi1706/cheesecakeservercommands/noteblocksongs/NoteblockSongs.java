package com.itachi1706.cheesecakeservercommands.noteblocksongs;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Song;
import com.itachi1706.cheesecakeservercommands.noteblocksongs.objects.NoteblockSong;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class NoteblockSongs {
	private static int currentIndex = 0;
	public static List<Song> songs = new ArrayList<>();
	public static List<String> names = new ArrayList<>();
	private static Iterator<Song> current = songs.iterator();
	public static boolean playing = false, random = false;
	public static NoteblockSong player;
	public static float volume = 1;
	public static int messageState = 0; // Defaults to MESSAGE_STATE_CHAT

	public static final int MESSAGE_STATE_CHAT = 0, MESSAGE_STATE_INFO = 1, MESSAGE_STATE_ALL = 2;

	private NoteblockSongs() {
	    throw new IllegalStateException("This is a utility class");
    }
	
	public static void onSongEnd() {
		if (playing) next();
	}
	
	public static void sendMsg(@Nullable CommandSourceStack sender) {
		try {
            /*
              What this prints:
              	Now playing: {AUTHOR} - {SONG_NAME}
              	Tooltip: {SONG_NAME}
              	Author: {AUTHOR}
              	Original author: {ORIG_AUTHOR}
              	Description:
              	{DESC}
              	Length: {SONG_LENGTH}
             */
			Gson gson = new Gson();
			JsonArray arr = new JsonArray();
			arr.add("");

			/*
				[
					"",
					{
						"text": "Now playing: ",
						"color": "gold"
					},
					{
						"text": "Song Name",
						"color": "green",
						"hoverEvent": {
							"action": "show_text",
							"value": [
								"",
								{
									"text": "song name\n",
									"color": "green"
								},
								"Author: ",
								{
									"text": "authorname\n",
									"color": "gold"
								},
								"Original author: ",
								{
									"text": "orig author\n",
									"color": "gold"
								},
								"Description: \n",
								{
									"text": "descriptionhere\n",
									"italic": true
								},
								"Length: ",
								{
									"text": "playerLen",
									"color": "gold"
								}
							],
							"color": "green"
						}
					}
				]
			 */

			// Now Playing Element
			JsonObject nowPlaying = new JsonObject();
			nowPlaying.addProperty("text", "Now playing: ");
			nowPlaying.addProperty("color", "gold");
			arr.add(nowPlaying);

			// Song Name Element
			JsonObject songName = new JsonObject();
			songName.addProperty("text", names.get(currentIndex));
			songName.addProperty("color", "green");

			String chatTextWithoutHover = gson.toJson(arr);

			// Hover Event
			JsonObject hoverEvent = new JsonObject();
			hoverEvent.addProperty("action", "show_text");

			JsonArray hoverValues = new JsonArray();
			hoverValues.add("");

			// Hover Event "Song Name"
			JsonObject hoverSongName = new JsonObject();
			hoverSongName.addProperty("text", (player.song.getName().trim().isEmpty() ? names.get(currentIndex) : player.song.getName()) + "\\n");
			hoverSongName.addProperty("color", "green");
			hoverValues.add(hoverSongName);

			// Hover Event Author if exist
			if (!player.song.getAuthor().trim().equals("")) {
				hoverValues.add("Author: ");
				JsonObject hoverAuthorName = new JsonObject();
				hoverAuthorName.addProperty("text", player.song.getAuthor() + "\\n");
				hoverAuthorName.addProperty("color", "gold");
				hoverValues.add(hoverAuthorName);
			}

			// Hover Event Original Author if exist
			if (!player.song.getOriginalAuthor().trim().equals("")) {
				hoverValues.add("Original author: ");
				JsonObject hoverOriginalAuthorName = new JsonObject();
				hoverOriginalAuthorName.addProperty("text", player.song.getOriginalAuthor() + "\\n");
				hoverOriginalAuthorName.addProperty("color", "gold");
				hoverValues.add(hoverOriginalAuthorName);
			}

			// Hover Event Description if exist
			if (!player.song.getDescription().trim().equals("")) {
				hoverValues.add("Description: \\n");
				JsonObject hoverDescription = new JsonObject();
				hoverDescription.addProperty("text", player.song.getDescription() + "\\n");
				hoverDescription.addProperty("italic", "true");
				hoverValues.add(hoverDescription);
			}

			hoverValues.add("Length: ");
			JsonObject length = new JsonObject();
			length.addProperty("text", getTimeString(player.length));
			length.addProperty("color", "gold");


			hoverEvent.add("value", hoverValues);
			hoverEvent.addProperty("color", "green");
			songName.add("hoverEvent", hoverEvent);

			arr.add(songName);

			String chatText = gson.toJson(arr);

            LogHelper.info("NBS: Playing " + names.get(currentIndex));
            if (sender == null) {
                switch (messageState) {
                    case MESSAGE_STATE_CHAT:
						TextUtil.sendGlobalChatMessage(ServerUtil.getServerPlayers(), chatText);
                        break;
                    case MESSAGE_STATE_INFO:
						TextUtil.sendGlobalActionMessage(ServerUtil.getServerPlayers(), chatTextWithoutHover);
                        break;
                    default:
						TextUtil.sendGlobalChatMessage(ServerUtil.getServerPlayers(), chatText);
						TextUtil.sendGlobalActionMessage(ServerUtil.getServerPlayers(), chatTextWithoutHover);
                        break;

                }
            } else {
				sender.sendSuccess(new TextComponent(chatText), false);
            }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts number of ticks into readable time format.
	 * Example: 2520 ticks becomes "1:02" (1 second = 40 ticks).
	 *
	 * @param dur The input number of ticks.
	 * @return The time sring.
	 */
	private static String getTimeString(int dur) {
		int duration = dur-1;
		int secs = duration/40;
		int hours = secs/3600;
		int minutes = (secs%3600)/60;
		String minString = (minutes < 10 && hours > 0) ? "0" + minutes : Integer.toString(minutes);
		int seconds = secs%60;
		String secString = (seconds < 10 && secs > 59) ? "0" + seconds : Integer.toString(seconds);
		return (hours > 0 ? hours + "h" : "") + ((hours == 0 && minutes > 0) ? minString + ":" : "") + secString + (secs < 60 ? "\"" : "");
	}
	
	public static boolean isPlaying() {
		return playing;
	}
	
	/**
	 * Plays index-th song.
	 * @param sender Command Sender
	 * @param index The numerical order of the song in the list.
	 */
	public static void play(CommandSourceStack sender, int index) {
		if (playing) player.stop();
		if (songs.isEmpty()) {
			sender.sendFailure(new TextComponent(
					ChatFormatting.RED + "There are not any songs in songs folder..."
			));
			return;
		}
		current = songs.iterator();
		if (random) index = new Random().nextInt(songs.size());
		currentIndex = index;
		if (index > 0) for (int i = 0; i < index; i++) current.next();
		player = new NoteblockSong(current.next());
		playing = true;
		player.start();
		sendMsg(null);
	}
	
	/**
	 * Plays the first song.
	 */
	public static void play(CommandSourceStack sender) {
		play(sender,0);
	}
	
	/**
	 * Plays the next song.
	 */
	public static void next() {
		player.stop();
		if (random) {
		    current = songs.iterator();
		    currentIndex = new Random().nextInt(songs.size());
		    if (currentIndex > 0) for (int i = 0; i < currentIndex; i++) current.next();
        } else {
            if (!current.hasNext()) {
                current = songs.iterator();
                currentIndex = 0;
            } else currentIndex++;
        }
		player = new NoteblockSong(current.next());
		player.start();
		sendMsg(null);
	}
	
	/**
	 * Pauses playing.
	 */
	private static void stop() {
		if (playing) player.stop();
		playing = false;
		random = false;
	}
	
	/**
	 * Refreshes the song list.
	 */
	public static void refreshSongs() {
		stop();
		player = null;
		String path = CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + "/songs";
		File songsFolder = new File(path);
		File[] files = songsFolder.listFiles();
		songs = new ArrayList<>();
		names = new ArrayList<>();
		if (files != null) for (File f : files) {
			if (f.getName().toLowerCase().endsWith(".nbs"))
				try {
					Song s = new Song(f);
					songs.add(s);
					names.add(s.getName().trim().equals("") ? f.getName() : s.getAuthor() + " - " + s.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
