package com.itachi1706.cheesecakeservercommands.server.storage;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.storage
 */
public class PlayerIPStorage {

    private UUID uuid;
    private String ipAddr;

    public PlayerIPStorage(){}
    public PlayerIPStorage(UUID uuid, String ip){
        this.uuid = uuid;
        ipAddr = ip;
    }

    public void setPlayerIP(String ip){
        ipAddr = ip;
    }

    public String getPlayerIP(){
        return ipAddr;
    }

    public void setUuid(UUID uuid){
        this.uuid = uuid;
    }

    public UUID getUuid(){
        return uuid;
    }

}
