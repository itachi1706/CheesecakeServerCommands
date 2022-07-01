package com.itachi1706.cheesecakeservercommands.noteblocksongs.objects;

import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Layer;
import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Note;
import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Song;
import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.List;

public class NoteblockSong {
	private final Song song;
	private boolean playing = false;
	private final int speed; // Default 4
	private int currentTick = 0;
	private final int length;
	private static final String[] names = {"harp", "bass", "basedrum", "snare", "hat", "guitar", "flute", "bell", "chime", "xylophone"};

	// Getters and Setters
	public Song getSong() {
		return song;
	}

	public int getLength() {
		return length;
	}

	// Methods
	public void onTick() {
		if (playing) currentTick++;
		if (currentTick > length) {
			stop();
			NoteblockSongs.onSongEnd();
		}
		// Plays the notes at the current tick.
		List<NoteblockNote> toPlay = getNotesAt(currentTick/speed);
		if (toPlay.isEmpty()) return;
		for (NoteblockNote n : getNotesAt(currentTick/speed)) {
			float pitch = (float)Math.pow(2.0D, (n.getNote().getPitch() - 45) / 12.0D);

			for (ServerLevel s : ServerUtil.getServerInstance().getAllLevels()) {
				if (s.players().isEmpty()) continue;
				for (ServerPlayer player : s.players()) {
					ResourceLocation sound = new ResourceLocation("minecraft", "block.note_block." + names[n.getNote().getInstrument().getID()]);
					player.connection.send(new ClientboundCustomSoundPacket(sound, SoundSource.RECORDS, player.position(), NoteblockSongs.getVolume() * n.getVolume(), pitch));
				}
			}
		}
	}
	
	private int getSongLength() {
		int res = -1;
		for (Layer l : song.getSongBoard()) {
			int max = -1;
			for (int i : l.getNoteList().keySet()) max = Math.max(max, i);
			res = Math.max(res, max);
		}
		return res*speed+120;
	}
	
	private List<NoteblockNote> getNotesAt(int tick) {
		List<NoteblockNote> res = new ArrayList<>();
		for (Layer l : song.getSongBoard()) {
			Note n = l.getNoteList().get(tick);
			if (n != null) res.add(new NoteblockNote(n, l.getVolume()/100f));
		}
		return res;
	}
	
	public NoteblockSong(Song song) {
		this.song = song;
		speed = 40/(this.song.getTempo()/100);
		length = getSongLength();
	}
	
	public void start() {
		currentTick = -1;
		playing = true;
	}
	
	public void stop() {
		playing = false;
	}
}
