package com.itachi1706.cheesecakeservercommands.jsonstorage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.jsonstorage
 */
public class PermissionsHelper {

    public static final String PERMISSION_FILENAME = "playerperms.json";

    public static void writeToFile(){
        //LastKnownUsernames[] loginArray = CheesecakeServerCommands.lastKnownUsernames.toArray(new LastKnownUsernames[CheesecakeServerCommands.lastKnownUsernames.size()]);

        Gson gson = new Gson();
        String jsonString = gson.toJson(CheesecakeServerCommands.playerPermissions);

        try{
            FileWriter writer = new FileWriter(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + PERMISSION_FILENAME);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            LogHelper.error("Cannot write permissions file");
            e.printStackTrace();
        }
    }

    public static HashMap<UUID, PlayerPermissions> readFromFile(){
        Gson gson = new Gson();
        try{
            BufferedReader br = new BufferedReader(new FileReader(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + PERMISSION_FILENAME));
            Type type = new TypeToken<HashMap<UUID, PlayerPermissions>>(){}.getType();
            HashMap<UUID, PlayerPermissions> permissions = gson.fromJson(br, type);
            LogHelper.info("Loaded " + permissions.size() + " player's permissions from file");
            return permissions;
        } catch (FileNotFoundException e) {
            LogHelper.error("Unable to read file");
            e.printStackTrace();
        } catch (NullPointerException ex) {
            LogHelper.error("Empty file!");
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean fileExists() {
        File file = new File(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + PERMISSION_FILENAME);
        return file.exists();
    }

}
