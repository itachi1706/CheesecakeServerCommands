package com.itachi1706.cheesecakeservercommands.jsonstorage;

import com.google.gson.Gson;
import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kenneth on 15/7/2018.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.jsonstorage
 */
public class MP3SongsJsonHelper {

    public static void writeToFile(){
        MP3Songs[] loginArray = CheesecakeServerCommands.mp3SongsList.toArray(new MP3Songs[0]);

        Gson gson = new Gson();
        String jsonString = gson.toJson(loginArray);

        try{
            FileWriter writer = new FileWriter(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "mp3.json");
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            LogHelper.error("Cannot write usernames file");
            e.printStackTrace();
        }
    }

    public static List<MP3Songs> readFromFile(){
        Gson gson = new Gson();
        try{
            BufferedReader br = new BufferedReader(new FileReader(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "mp3.json"));

            MP3Songs[] mp3Songs = gson.fromJson(br, MP3Songs[].class);
            LogHelper.info("Loaded " + mp3Songs.length + " MP3 Files from file");
            return new ArrayList<>(Arrays.asList(mp3Songs));
        } catch (FileNotFoundException e) {
            LogHelper.error("Unable to read file");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean fileExists() {
        File file = new File(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "mp3.json");
        return file.exists();
    }

    public static boolean addSongs(MP3Songs song){
        for (MP3Songs s : CheesecakeServerCommands.mp3SongsList) {
            if (s.isIdentical(song)) {
                return false; // Already added
            }
        }

        CheesecakeServerCommands.mp3SongsList.add(song);
        writeToFile();
        return true;
    }

    public static boolean removeSongs(MP3Songs song){
        for (MP3Songs s : CheesecakeServerCommands.mp3SongsList) {
            if (s.isIdentical(song)) {
                CheesecakeServerCommands.mp3SongsList.remove(s);
                writeToFile();
                return true;
            }
        }
        return false; // Cannot remove
    }

    @SuppressWarnings("unused")
    private MP3Songs getSample() {
        return new MP3Songs("Sample Song", "sample.mp3", "http://localhost/sample.mp3", 10);
    }

}
