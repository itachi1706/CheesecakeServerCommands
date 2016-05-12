package com.itachi1706.cheesecakeservercommands.jsonstorage;

/**
 * Created by Kenneth on 10/5/2016.
 * for com.itachi1706.cheesecakeservercommands.jsonstorage in CheesecakeServerCommands
 */
public class PermissionObject {

    public String key;
    public boolean granted;

    public PermissionObject(String key, boolean granted) {
        this.key = key;
        this.granted = granted;
    }

    public PermissionObject(String key) {
        this.key = key;
        this.granted = true;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }
}
