package com.itachi1706.cheesecakeservercommands.mojangcmd;

import net.minecraft.util.text.TextFormatting;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Kenneth on 9/12/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.mojangcmd
 */
public enum MojangStatusChecker {
    ACCOUNTS("Accounts Service", "account.mojang.com"),
    AUTHENTICATION("Yggdrasil Authentication Server", "auth.mojang.com"),
    AUTHENTICATION_SERVER("Game Authentication Service", "authserver.mojang.com"),
    LOGIN("Login Service (Legacy)", "login.minecraft.net"),
    SESSION_MOJANG("Minecraft Session Server", "sessionserver.mojang.com"),
    SKINS("Skin Server", "skins.minecraft.net"),
    MAIN_WEBSITE("Minecraft Website", "minecraft.net");

    private String name, serviceURL;
    private JSONParser jsonParser = new JSONParser();

    MojangStatusChecker(String name, String serviceURL) {
        this.name = name;
        this.serviceURL = serviceURL;
    }

    public String getName() {
        return name;
    }

    /**
     * Check the current Mojang service for it's status, errors are ignored.
     *
     * @return Status of the service.
     */
    public Status getStatus() {
        return getStatus(true);
    }

    /**
     * Check the current Mojang service for it's status.
     *
     * @param suppressErrors - Don't print errors in console.
     * @return Status of the service.
     */
    public Status getStatus(boolean suppressErrors) {
        try {
            URL url = new URL("http://status.mojang.com/check?service=" + serviceURL);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

            Object object = jsonParser.parse(bufferedReader);
            JSONObject jsonObject = (JSONObject) object;

            String status = (String) jsonObject.get(serviceURL);

            return Status.get(status);

        } catch (IOException exception) {
            if (!suppressErrors) {
                exception.printStackTrace();
            }

            return Status.UNKNOWN;
        } catch (ParseException exception) {
            if (!suppressErrors) {
                exception.printStackTrace();
            }

            return Status.UNKNOWN;
        }
    }

    public enum Status {
        ONLINE("Online", TextFormatting.GREEN.toString(), "No problems detected! :)"),
        UNSTABLE("Unstable", TextFormatting.YELLOW.toString(), "Intermittent Connection :("),
        OFFLINE("Offline", TextFormatting.DARK_RED.toString(), "Currently Offline! D:"),
        UNKNOWN("Unknown", TextFormatting.WHITE.toString(), "Unable to connect to Mojang Server!");

        private String status, color, description;

        Status(String status, String color, String description) {
            this.status = status;
            this.color = color;
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public String getColor() {
            return color;
        }

        public String getDescription() {
            return description;
        }

        public static Status get(String status) {
            status = status.toLowerCase();

            if (status.equals("green")) {
                return Status.ONLINE;
            } else if (status.equals("yellow")) {
                return Status.UNSTABLE;
            } else if (status.equals("red")) {
                return Status.OFFLINE;
            } else {
                return Status.UNKNOWN;
            }
        }
    }

}
