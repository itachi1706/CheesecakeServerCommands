package com.itachi1706.cheesecakeservercommands.reference;

public class CommandPermissionsLevel {

    private CommandPermissionsLevel() {
        throw new IllegalStateException("Reference Class");
    }

    /**
     * Everyone can use this command
     */
    public static final int ALL = 0;

    /**
     * Users are not operators but can bypass spawn and have some commands
     */
    public static final int BYPASS_SPAWN = 1;

    /**
     * Server operators can use this command
     */
    public static final int OPS = 2;

    /**
     * Server operators with ability to manage players can use this command
     */
    public static final int SERVER = 3;

    /**
     * Server operators that can manage the server can use this command
     */
    public static final int CONSOLE = 4;
}
