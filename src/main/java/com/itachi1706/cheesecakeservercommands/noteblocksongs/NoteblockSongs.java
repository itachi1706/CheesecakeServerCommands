package com.itachi1706.cheesecakeservercommands.noteblocksongs;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Song;
import com.itachi1706.cheesecakeservercommands.noteblocksongs.objects.NoteblockSong;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.json.simple.JSONObject;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class NoteblockSongs {
	private static int currentIndex = 0;
	public static List<Song> songs = new ArrayList<Song>();
	public static List<String> names = new ArrayList<String>();
	private static Iterator<Song> current = songs.iterator();
	public static boolean playing = false, random = false;
	public static NoteblockSong player;
	public static float volume = 1;
	public static int messageState = 0; // Defaults to MESSAGE_STATE_CHAT

	public static final int MESSAGE_STATE_CHAT = 0, MESSAGE_STATE_INFO = 1, MESSAGE_STATE_ALL = 2;
	
	public static void onSongEnd() {
		if (playing) next();
	}
	
	public static void sendMsg(@Nullable ICommandSender sender) {
		try {
            /**
             * What this prints:
             * 	Now playing: {AUTHOR} - {SONG_NAME}
             * 	Tooltip: {SONG_NAME}
             * 	Author: {AUTHOR}
             * 	Original author: {ORIG_AUTHOR}
             * 	Description:
             * 	{DESC}
             * 	Length: {SONG_LENGTH}
             */
			String toTell = "[\"\",{\"text\":\"Now playing: \",\"color\":\"gold\"},{\"text\":\"" + JSONObject.escape(names.get(currentIndex))
			+ "\",\"color\":\"green\"";
			StringBuilder chatText = new StringBuilder();
			chatText.append(toTell).append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"")
                    .append(JSONObject.escape(player.song.getName().trim().isEmpty() ? names.get(currentIndex) : player.song.getName()))
                    .append("\\n\",\"color\":\"green\"}");
			if (!player.song.getAuthor().trim().equals("")) chatText.append(",\"Author: \",{\"text\":\"")
                    .append(JSONObject.escape(player.song.getAuthor())).append("\\n\",\"color\":\"gold\"}");
			if (!player.song.getOriginalAuthor().trim().equals("")) chatText.append(",\"Original author: \",{\"text\":\"")
                    .append(JSONObject.escape(player.song.getOriginalAuthor())).append("\\n\",\"color\":\"gold\"}");
			if (!player.song.getDescription().trim().equals("")) chatText.append(",\"Description: \\n\",{\"text\":\"")
                    .append(JSONObject.escape(player.song.getDescription())).append("\\n\",\"italic\":true}");
			chatText.append(",\"Length: \",{\"text\":\"").append(getTimeString(player.length).replaceAll("\"", "\\\\\\\""))
                    .append("\",\"color\":\"gold\"}],\"color\":\"green\"}}]");

            LogHelper.info("NBS: Playing " + names.get(currentIndex));
            if (sender == null) {
                switch (messageState) {
                    case MESSAGE_STATE_CHAT:
                        ChatHelper.sendGlobalMessage(ITextComponent.Serializer.jsonToComponent(chatText.toString()));
                        break;
                    case MESSAGE_STATE_INFO:
                        ChatHelper.sendGlobalInfoMessage(ITextComponent.Serializer.jsonToComponent(toTell + "}]"));
                        break;
                    default:
                        ChatHelper.sendGlobalMessage(ITextComponent.Serializer.jsonToComponent(chatText.toString()));
                        ChatHelper.sendGlobalInfoMessage(ITextComponent.Serializer.jsonToComponent(toTell + "}]"));
                        break;

                }
            } else {
                ChatHelper.sendMessage(sender, ITextComponent.Serializer.jsonToComponent(chatText.toString()));
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
		String minString = (minutes < 10 && hours > 0) ? "0" + minutes : minutes + "";
		int seconds = secs%60;
		String secString = (seconds < 10 && secs > 59) ? "0" + seconds : seconds + "";
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
	public static void play(ICommandSender sender, int index) {
		if (playing) player.stop();
		if (songs.isEmpty()) {
			ChatHelper.sendMessage(sender, new TextComponentString(
					TextFormatting.RED + "There are not any songs in songs folder..."
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
	public static void play(ICommandSender sender) {
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
