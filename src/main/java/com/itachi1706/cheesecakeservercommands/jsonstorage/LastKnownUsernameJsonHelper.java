package com.itachi1706.cheesecakeservercommands.jsonstorage;

import com.google.gson.Gson;
import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.jsonstorage
 */
public class LastKnownUsernameJsonHelper {

    public static void writeToFile(){
        LastKnownUsernames[] loginArray = CheesecakeServerCommands.lastKnownUsernames.toArray(new LastKnownUsernames[CheesecakeServerCommands.lastKnownUsernames.size()]);

        Gson gson = new Gson();
        String jsonString = gson.toJson(loginArray);

        try{
            FileWriter writer = new FileWriter(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "usernames.json");
            writer.write(jsonString);
            writer.close();
            LogHelper.info("Wrote usernames to file");
        } catch (IOException e) {
            LogHelper.error("Cannot write usernames file");
            e.printStackTrace();
        }
    }

    public static List<LastKnownUsernames> readFromFile(){
        Gson gson = new Gson();
        try{
            BufferedReader br = new BufferedReader(new FileReader(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "usernames.json"));

            LastKnownUsernames[] loginses = gson.fromJson(br, LastKnownUsernames[].class);
            LogHelper.info("Loaded " + loginses.length + " usernames from file");
            return new ArrayList<LastKnownUsernames>(Arrays.asList(loginses));
        } catch (FileNotFoundException e) {
            LogHelper.error("Unable to read file");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean fileExists() {
        File file = new File(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "usernames.json");
        return file.exists();
    }

    public static void logLastSeenToList(EntityPlayer player){
        LastKnownUsernames playerLastSeen = new LastKnownUsernames(player.getUniqueID(), player.getDisplayName(), System.currentTimeMillis());
        for (LastKnownUsernames i : CheesecakeServerCommands.lastKnownUsernames) {
            if (i.getUuid().equals(player.getUniqueID())) {
                CheesecakeServerCommands.lastKnownUsernames.remove(i);
                playerLastSeen = i;
                break;
            }
        }

        if (playerLastSeen.getFirstJoined() == 0){
            playerLastSeen.setFirstJoined(System.currentTimeMillis());
        }
        playerLastSeen.updateLastSeen();
        CheesecakeServerCommands.lastKnownUsernames.add(playerLastSeen);
        writeToFile();
    }

    public static void logUsernameToList(EntityPlayer player){
        LastKnownUsernames playerUsername = new LastKnownUsernames(player.getUniqueID(), player.getDisplayName(), System.currentTimeMillis());
        for (LastKnownUsernames i : CheesecakeServerCommands.lastKnownUsernames) {
            if (i.getUuid().equals(player.getUniqueID())) {
                CheesecakeServerCommands.lastKnownUsernames.remove(i);
                playerUsername = i;
                break;
            }
        }

        playerUsername.updateDisplayName(player.getDisplayName());
        CheesecakeServerCommands.lastKnownUsernames.add(playerUsername);
        writeToFile();
    }

    public static String getLastKnownPlayerNameFromUUID(UUID uuid){
        for (LastKnownUsernames lastKnownUsernames : CheesecakeServerCommands.lastKnownUsernames){
            if (lastKnownUsernames.getUuid().equals(uuid)){
                return lastKnownUsernames.getLastKnownUsername();
            }
        }

        return null;
    }

    public static UUID getLastKnownUUIDFromPlayerName(String playerName){
        for (LastKnownUsernames lastKnownUsernames : CheesecakeServerCommands.lastKnownUsernames){
            if (lastKnownUsernames.getLastKnownUsername().equals(playerName)){
                return lastKnownUsernames.getUuid();
            }
        }

        return null;
    }

    public static LastKnownUsernames getLastKnownUsernameFromList(UUID uuid){
        for (LastKnownUsernames lastKnownUsernames : CheesecakeServerCommands.lastKnownUsernames){
            if (lastKnownUsernames.getUuid().equals(uuid)){
                return lastKnownUsernames;
            }
        }

        return null;
    }

}
