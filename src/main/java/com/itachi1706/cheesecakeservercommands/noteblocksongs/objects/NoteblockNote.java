package com.itachi1706.cheesecakeservercommands.noteblocksongs.objects;

import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Note;

public class NoteblockNote {
	private final Note note;
	private final float volume;
	
	public NoteblockNote(Note note, float volume) {
		this.note = note;
		this.volume = volume;
	}

	public Note getNote() {
		return note;
	}

	public float getVolume() {
		return volume;
	}
}
