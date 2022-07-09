package com.itachi1706.cheesecakeservercommands.objects;

import net.minecraft.ChatFormatting;

/**
 * Created by Kenneth on 11/7/2016.
 * for com.itachi1706.cheesecakeservercommands.server.objects in CheesecakeServerCommands
 */
public class HelpSub {
    private final String command;
    private final String usage;

    public HelpSub(String command) {
        this.command = command;
        this.usage = ChatFormatting.RED + "No Usage";
    }

    public HelpSub(String command, String usage) {
        this.command = command;
        this.usage = usage;
    }

    public String getCommand() {
        return command;
    }

    public String getUsage() {
        return usage;
    }
}

