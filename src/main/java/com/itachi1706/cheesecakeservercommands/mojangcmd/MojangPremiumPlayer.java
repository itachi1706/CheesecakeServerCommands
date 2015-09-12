package com.itachi1706.cheesecakeservercommands.mojangcmd;

import com.itachi1706.cheesecakeservercommands.util.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Kenneth on 9/12/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.mojangcmd
 */
public class MojangPremiumPlayer {

    //URL = minecraft.net/haspaid.jsp?user=<player name>
    public static int isPremium(String name){
        //return 0 for false, 1 for true, 2 for error
        try {
            URL url = new URL("https://minecraft.net/haspaid.jsp?user=" + name);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            String check;
            if ((check = bufferedReader.readLine()) != null){
                buffer.append(check);
                if (check.contains("true")){
                    LogHelper.info("Premium check for " + name + ": True");
                    return 1;
                } else if (check.contains("false")){
                    LogHelper.info("Premium check for " + name + ": False");
                    return 0;
                } else {
                    LogHelper.error("Error parsing text. (Neither true nor false)");
                    return 2;
                }
            } else {
                return 2;
            }
        } catch (IOException e){
            LogHelper.error("An exception occured (IOException), " + e.getLocalizedMessage());
            return 2;
        } catch (Exception e){
            LogHelper.error("An exception occured, " + e.getLocalizedMessage());
            e.printStackTrace();
            return 2;
        }
    }
    
}
