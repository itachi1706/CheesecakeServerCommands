package com.itachi1706.cheesecakeservercommands.nbtstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 1/7/2018.
 * for com.itachi1706.cheesecakeservercommands.nbtstorage in CheesecakeServerCommands
 */
public class AdminSilenced {

    static List<UUID> silencedList = new ArrayList<>();
    static List<String> ignoredCommandUser = new ArrayList<>();
    static boolean enabled = false;

    public static final String MY_UUID = "fee9070f-bfaf-49df-8013-95320a7c5e68";

    static void reinit() {
        silencedList.clear();
    }

    public static boolean addUUID(CSCAdminSilenceWorldSavedData nbt, UUID uuid) {
        if (silencedList.contains(uuid)) return false;
        silencedList.add(uuid);
        nbt.setDirty();
        return true;
    }

    public static boolean removeUUID(CSCAdminSilenceWorldSavedData nbt, UUID uuid) {
        if (!silencedList.contains(uuid)) return false;
        silencedList.remove(uuid);
        nbt.setDirty();
        return true;
    }

    public static List<UUID> list() {
        return silencedList;
    }

    public static boolean contains(UUID uuid) {
        return silencedList.contains(uuid);
    }

    public static boolean enable(CSCAdminSilenceWorldSavedData nbt) {
        if (enabled) return false;
        enabled = true;
        nbt.setDirty();
        return true;
    }

    public static boolean disable(CSCAdminSilenceWorldSavedData nbt) {
        if (!enabled) return false;
        enabled = false;
        nbt.setDirty();
        return true;
    }

    public static void ignoreCommandUser(CSCAdminSilenceWorldSavedData nbt, String userName) {
        ignoredCommandUser.add(userName);
        nbt.setDirty();
    }

    public static void unignoreCommandUser(CSCAdminSilenceWorldSavedData nbt, String userName) {
        ignoredCommandUser.remove(userName);
        nbt.setDirty();
    }

    public static boolean isIgnored(String userName) {
        return ignoredCommandUser.contains(userName);
    }

    public static boolean getState() {
        return enabled;
    }
}
