package com.itachi1706.cheesecakeservercommands.jsonstorage;

import com.google.gson.Gson;
import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

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

    private LastKnownUsernameJsonHelper() {
        throw new IllegalStateException("Utility class");
    }

    private static final String USERNAME_FILE = "usernames.json";

    public static void writeToFile(){
        LastKnownUsernames[] loginArray = CheesecakeServerCommands.getLastKnownUsernames().toArray(new LastKnownUsernames[0]);

        Gson gson = new Gson();
        String jsonString = gson.toJson(loginArray);

        try (FileWriter writer = new FileWriter(CheesecakeServerCommands.getConfigFileDirectory().getAbsolutePath() + File.separator + USERNAME_FILE)) {
            writer.write(jsonString);
        } catch (IOException e) {
            LogHelper.error("Cannot write usernames file");
            e.printStackTrace();
        }
    }

    public static List<LastKnownUsernames> readFromFile(){
        Gson gson = new Gson();
        try{
            BufferedReader br = new BufferedReader(new FileReader(CheesecakeServerCommands.getConfigFileDirectory().getAbsolutePath() + File.separator + USERNAME_FILE));

            LastKnownUsernames[] loginses = gson.fromJson(br, LastKnownUsernames[].class);
            LogHelper.info("Loaded " + loginses.length + " usernames from file");
            return new ArrayList<>(Arrays.asList(loginses));
        } catch (FileNotFoundException e) {
            LogHelper.error("Unable to read file");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean fileExists() {
        File file = new File(CheesecakeServerCommands.getConfigFileDirectory().getAbsolutePath() + File.separator + USERNAME_FILE);
        return file.exists();
    }

    public static void logGamemodeToLit(ServerPlayer player){
        LastKnownUsernames playerLastSeen = new LastKnownUsernames(player.getUUID(), player.getDisplayName().getString(), System.currentTimeMillis());
        for (LastKnownUsernames i : CheesecakeServerCommands.getLastKnownUsernames()) {
            if (i.getUuid().equals(player.getUUID())) {
                CheesecakeServerCommands.getLastKnownUsernames().remove(i);
                playerLastSeen = i;
                break;
            }
        }

        playerLastSeen.setLastKnownGamemode(player.gameMode.getGameModeForPlayer().getName());

        CheesecakeServerCommands.getLastKnownUsernames().add(playerLastSeen);
        writeToFile();
    }

    public static void logLastSeenToList(Player player, boolean state){
        LastKnownUsernames playerLastSeen = new LastKnownUsernames(player.getUUID(), player.getDisplayName().getString(), System.currentTimeMillis());
        for (LastKnownUsernames i : CheesecakeServerCommands.getLastKnownUsernames()) {
            if (i.getUuid().equals(player.getUUID())) {
                CheesecakeServerCommands.getLastKnownUsernames().remove(i);
                playerLastSeen = i;
                break;
            }
        }

        if (playerLastSeen.getFirstJoined() == 0){
            playerLastSeen.setFirstJoined(System.currentTimeMillis());
        }

        // true if Login, false if Logout
        playerLastSeen.setLoginState(state);

        playerLastSeen.updateLastSeen();
        CheesecakeServerCommands.getLastKnownUsernames().add(playerLastSeen);
        writeToFile();
    }

    public static void logUsernameToList(Player player){
        LastKnownUsernames playerUsername = new LastKnownUsernames(player.getUUID(), player.getDisplayName().getString(), System.currentTimeMillis());
        for (LastKnownUsernames i : CheesecakeServerCommands.getLastKnownUsernames()) {
            if (i.getUuid().equals(player.getUUID())) {
                CheesecakeServerCommands.getLastKnownUsernames().remove(i);
                playerUsername = i;
                break;
            }
        }

        playerUsername.updateDisplayName(player.getDisplayName().getString());
        CheesecakeServerCommands.getLastKnownUsernames().add(playerUsername);
        writeToFile();
    }

    public static UUID getLastKnownUUIDFromPlayerName(String playerName){
        for (LastKnownUsernames lastKnownUsernames : CheesecakeServerCommands.getLastKnownUsernames()){
            if (lastKnownUsernames.getLastKnownUsername().equals(playerName)){
                return lastKnownUsernames.getUuid();
            }
        }

        return null;
    }

    public static LastKnownUsernames getLastKnownUsernameFromList(UUID uuid){
        for (LastKnownUsernames lastKnownUsernames : CheesecakeServerCommands.getLastKnownUsernames()){
            if (lastKnownUsernames.getUuid().equals(uuid)){
                return lastKnownUsernames;
            }
        }

        return null;
    }

}
