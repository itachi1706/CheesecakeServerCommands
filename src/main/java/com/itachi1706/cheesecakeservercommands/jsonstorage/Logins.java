package com.itachi1706.cheesecakeservercommands.jsonstorage;

import java.util.UUID;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.jsonstorage
 */
public class Logins {

    private UUID uuid;
    private String name;
    private long time;
    private String ip;
    private int x,y,z;
    private String world;
    private String type;

    public Logins(UUID uuid, String name, long time, String ip, int x, int y, int z, String world, String type) {
        this.uuid = uuid;
        this.name = name;
        this.time = time;
        this.ip = ip;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.type = type;
    }

    public Logins(UUID uuid, String name, String ip, int x, int y, int z, String world, String type) {
        this.time = System.currentTimeMillis();
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.type = type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
