package com.itachi1706.cheesecakeservercommands.jsonstorage;

/**
 * Created by Kenneth on 15/7/2018.
 * for com.itachi1706.cheesecakeservercommands.jsonstorage in CheesecakeServerCommands
 */
public class MP3Songs {

    private String songName, fileName, fileURL;
    private long duration;

    public MP3Songs() {
    }

    public MP3Songs(String songName, String fileName, String fileURL, long duration) {
        this.songName = songName;
        this.fileName = fileName;
        this.fileURL = fileURL;
        this.duration = duration;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isIdentical(MP3Songs otherSong) {
        if (!otherSong.getFileName().equalsIgnoreCase(this.fileName)) return false;
        if (otherSong.getDuration() != this.duration) return false;
        if (!otherSong.getFileURL().equalsIgnoreCase(this.fileURL)) return false;
        return otherSong.getSongName().equalsIgnoreCase(this.songName);
    }
}
