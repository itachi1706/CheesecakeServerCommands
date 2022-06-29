package com.itachi1706.cheesecakeservercommands.noteblocksongs.objects;

import com.itachi1706.cheesecakeservercommands.libs.nbsapi.Note;

public class NoteblockNote {
	public Note note;
	public float volume;
	
	public NoteblockNote(Note note, float volume) {
		this.note = note;
		this.volume = volume;
	}
}
