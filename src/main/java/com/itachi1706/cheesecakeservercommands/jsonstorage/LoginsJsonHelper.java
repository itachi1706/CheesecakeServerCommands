package com.itachi1706.cheesecakeservercommands.jsonstorage;

import com.google.gson.Gson;
import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.jsonstorage
 */
public class LoginsJsonHelper {

    public static void writeToFile(){
        Logins[] loginArray = CheesecakeServerCommands.loginLogoutLogger.toArray(new Logins[CheesecakeServerCommands.loginLogoutLogger.size()]);

        Gson gson = new Gson();
        String jsonString = gson.toJson(loginArray);

        try{
            FileWriter writer = new FileWriter(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "logins.json");
            writer.write(jsonString);
            writer.close();
            LogHelper.info("Wrote logins to file");
        } catch (IOException e) {
            LogHelper.error("Cannot write login/logout file");
            e.printStackTrace();
        }
    }

    public static List<Logins> readFromFile(){
        Gson gson = new Gson();
        try{
            BufferedReader br = new BufferedReader(new FileReader(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "logins.json"));

            Logins[] loginses = gson.fromJson(br, Logins[].class);
            LogHelper.info("Loaded logins from file");
            return Arrays.asList(loginses);
        } catch (FileNotFoundException e) {
            LogHelper.error("Unable to read file");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean fileExists() {
        File file = new File(CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "logins.json");
        return file.exists();
    }

}
