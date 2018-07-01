package com.itachi1706.cheesecakeservercommands.nbtstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 1/7/2018.
 * for com.itachi1706.cheesecakeservercommands.nbtstorage in CheesecakeServerCommands
 */
public class AdminSilenced {

    public static List<UUID> silencedList = new ArrayList<>();
    public static boolean enabled;

    public static void reinit() {
        silencedList.clear();
    }

    public static boolean addUUID(CSCAdminSilenceWorldSavedData nbt, UUID uuid) {
        if (silencedList.contains(uuid)) return false;
        silencedList.add(uuid);
        nbt.markDirty();
        return true;
    }

    public static boolean removeUUID(CSCAdminSilenceWorldSavedData nbt, UUID uuid) {
        if (!silencedList.contains(uuid)) return false;
        silencedList.remove(uuid);
        nbt.markDirty();
        return true;
    }

    public static List<UUID> list() {
        return silencedList;
    }

    public static boolean enable(CSCAdminSilenceWorldSavedData nbt) {
        if (enabled) return false;
        enabled = true;
        nbt.markDirty();
        return true;
    }

    public static boolean disable(CSCAdminSilenceWorldSavedData nbt) {
        if (!enabled) return false;
        enabled = false;
        nbt.markDirty();
        return true;
    }

    public static boolean getState() {
        return enabled;
    }
}
