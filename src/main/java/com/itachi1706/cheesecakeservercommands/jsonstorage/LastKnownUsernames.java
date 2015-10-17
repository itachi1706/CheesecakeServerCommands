package com.itachi1706.cheesecakeservercommands.jsonstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.jsonstorage
 */
public class LastKnownUsernames {

    private UUID uuid;
    private String lastKnownUsername;
    private List<String> historyOfKnownUsernames = new ArrayList<String>();
    private long lastSeen;
    private long firstJoined;
    private boolean loginState;
    private String lastKnownGamemode = "";

    public LastKnownUsernames(UUID uuid, String lastKnownUsername, long firstJoined) {
        this.uuid = uuid;
        this.lastKnownUsername = lastKnownUsername;
        this.firstJoined = firstJoined;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getLastKnownUsername() {
        return lastKnownUsername;
    }

    public void setLastKnownUsername(String lastKnownUsername) {
        this.lastKnownUsername = lastKnownUsername;
    }

    public List<String> getHistoryOfKnownUsernames() {
        return historyOfKnownUsernames;
    }

    public void addHistoryOfKnownUsernames(String username){
        for (String s : this.historyOfKnownUsernames){
            if (s.equals(username))
                return;
        }

        this.historyOfKnownUsernames.add(username);
    }

    public void updateDisplayName(String currentDisplayName){
        setLastKnownUsername(currentDisplayName);

        addHistoryOfKnownUsernames(currentDisplayName);
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void updateLastSeen() {
        setLastSeen(System.currentTimeMillis());
    }

    public long getFirstJoined() {
        return firstJoined;
    }

    public void setFirstJoined(long firstJoined) {
        this.firstJoined = firstJoined;
    }

    public boolean isLoginState() {
        return loginState;
    }

    public void setLoginState(boolean loginState) {
        this.loginState = loginState;
    }

    public String getLastKnownGamemode() {
        return lastKnownGamemode;
    }

    public void setLastKnownGamemode(String lastKnownGamemode) {
        this.lastKnownGamemode = lastKnownGamemode;
    }

    public boolean hasLastKnownGamemode(){
        return this.lastKnownGamemode != null;
    }
}
