package com.itachi1706.cheesecakeservercommands.noteblocksongs.objects;

import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Layer;
import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Note;
import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Song;
import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;

public class NoteblockSong {
	public Song song;
	private boolean playing = false;
	private int speed; // Default 4
	private int currentTick = 0;
	public int length;
	private static final String[] names = {"harp", "bass", "basedrum", "snare", "hat", "guitar", "flute", "bell", "chime", "xylophone"};
	
	public void onTick() {
		if (playing) currentTick++;
		if (currentTick > length) {
			stop();
			NoteblockSongs.onSongEnd();
		}
		// Plays the notes at the current tick.
		List<NoteblockNote> toPlay = getNotesAt(currentTick/speed);
		if (toPlay.size() > 0) for (NoteblockNote n : getNotesAt(currentTick/speed)) {
			float pitch = (float)Math.pow(2.0D, (double)(n.note.getPitch() - 45) / 12.0D);
			for (WorldServer s : DimensionManager.getWorlds()) {
				if (!s.playerEntities.isEmpty()) {
					for (EntityPlayer player : s.playerEntities) {
                        if (player instanceof EntityPlayerMP) {
                            EntityPlayerMP playerMP = (EntityPlayerMP) player;
                            playerMP.connection.sendPacket(new SPacketCustomSound("minecraft:block.note." +
                                    names[n.note.getInstrument().getID()], SoundCategory.RECORDS, playerMP.posX, playerMP.posY, playerMP.posZ,
                                    NoteblockSongs.volume * n.volume, pitch));
                        }
					}
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
