package com.itachi1706.cheesecakeservercommands.objects;

/**
 * Created by Kenneth on 11/7/2016.
 * for com.itachi1706.cheesecakeservercommands.server.objects in CheesecakeServerCommands
 */
public class HelpMain {

    private final String key;
    private final String name;
    private HelpSub[] commands;
    private final boolean adminOnly;

    public HelpMain(String key, HelpSub[] commands) {
        this.key = key;
        this.name = "Nameless Category";
        this.commands = commands;
        this.adminOnly = false;
    }

    public HelpMain(String key, HelpSub[] commands, boolean adminOnly) {
        this.key = key;
        this.name = "Nameless Category";
        this.commands = commands;
        this.adminOnly = adminOnly;
    }

    public HelpMain(String key, String name, HelpSub[] commands) {
        this.key = key;
        this.name = name;
        this.commands = commands;
        this.adminOnly = false;
    }

    public HelpMain(String key, String name, HelpSub[] commands, boolean adminOnly) {
        this.key = key;
        this.name = name;
        this.commands = commands;
        this.adminOnly = adminOnly;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public HelpSub[] getCommands() {
        return commands;
    }

    public void setCommands(HelpSub[] commands) {
        this.commands = commands;
    }

    public boolean isAdminOnly() {
        return adminOnly;
    }
}
