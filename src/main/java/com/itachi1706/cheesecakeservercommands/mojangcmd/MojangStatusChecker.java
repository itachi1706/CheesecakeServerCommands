package com.itachi1706.cheesecakeservercommands.mojangcmd;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.ChatFormatting;

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
    private JsonParser jsonParser = new JsonParser();

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

            JsonObject jsonObject = JsonParser.parseString(bufferedReader.toString()).getAsJsonObject();
            String status = jsonObject.get(serviceURL).getAsString();

            return Status.get(status);

        } catch (IOException | JsonParseException exception) {
            if (!suppressErrors) {
                exception.printStackTrace();
            }

            return Status.UNKNOWN;
        }
    }

    public enum Status {
        ONLINE("Online", ChatFormatting.GREEN.toString(), "No problems detected! :)"),
        UNSTABLE("Unstable", ChatFormatting.YELLOW.toString(), "Intermittent Connection :("),
        OFFLINE("Offline", ChatFormatting.DARK_RED.toString(), "Currently Offline! D:"),
        UNKNOWN("Unknown", ChatFormatting.WHITE.toString(), "Unable to connect to Mojang Server!");

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

            switch (status) {
                case "green":
                    return Status.ONLINE;
                case "yellow":
                    return Status.UNSTABLE;
                case "red":
                    return Status.OFFLINE;
                default:
                    return Status.UNKNOWN;
            }
        }
    }

}
