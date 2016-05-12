package com.itachi1706.cheesecakeservercommands.jsonstorage;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Kenneth on 10/5/2016.
 * for com.itachi1706.cheesecakeservercommands.jsonstorage in CheesecakeServerCommands
 */
public class PlayerPermissions {

    private UUID playerUUID;
    HashMap<String, PermissionObject> permissionList;

    public PlayerPermissions(UUID playerUUID, HashMap<String, PermissionObject> permissionList) {
        this.playerUUID = playerUUID;
        this.permissionList = permissionList;
    }

    public PlayerPermissions(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.permissionList = new HashMap<String, PermissionObject>();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public HashMap<String, PermissionObject> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(HashMap<String, PermissionObject> permissionList) {
        this.permissionList = permissionList;
    }
}
