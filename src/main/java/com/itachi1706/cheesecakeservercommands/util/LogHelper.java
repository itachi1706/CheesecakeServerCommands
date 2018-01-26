package com.itachi1706.cheesecakeservercommands.util;

import com.itachi1706.cheesecakeservercommands.reference.References;
import org.apache.logging.log4j.*;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.util
 */
public class LogHelper {

    public static final Marker MOD_MARKER = MarkerManager.getMarker(References.MOD_ID);
    private static final Logger LOGGER = LogManager.getLogger(References.MOD_ID);

    public static void log(Level level, Marker marker, Object  object) {
        LOGGER.log(level, marker, object);
    }

    public static void all(Object object)
    {
        log(Level.ALL, MOD_MARKER, object);
    }

    public static void debug(Object object)
    {
        log(Level.DEBUG, MOD_MARKER, object);
    }

    public static void error(Object object)
    {
        log(Level.ERROR, MOD_MARKER, object);
    }

    public static void fatal(Object object)
    {
        log(Level.FATAL, MOD_MARKER, object);
    }

    public static void info(Object object)
    {
        log(Level.INFO, MOD_MARKER, object);
    }

    public static void off(Object object)
    {
        log(Level.OFF, MOD_MARKER, object);
    }

    public static void trace(Object object)
    {
        log(Level.TRACE, MOD_MARKER, object);
    }

    public static void warn(Object object)
    {
        log(Level.WARN, MOD_MARKER, object);
    }

}
