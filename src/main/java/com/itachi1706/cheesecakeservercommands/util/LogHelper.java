package com.itachi1706.cheesecakeservercommands.util;

import com.itachi1706.cheesecakeservercommands.reference.References;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.util
 */
public class LogHelper {

    public static final Marker MOD_MARKER = MarkerFactory.getMarker(References.MOD_ID);
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void debug(String message, Object... object)
    {
        LOGGER.debug(MOD_MARKER, message, object);
    }

    public static void error(String message, Object... object)
    {
        LOGGER.error(MOD_MARKER, message, object);
    }

    public static void info(String message, Object... object)
    {
        LOGGER.info(MOD_MARKER, message, object);
    }

    public static void trace(String message, Object... object)
    {
        LOGGER.trace(MOD_MARKER, message, object);
    }

    public static void warn(String message, Object... object)
    {
        LOGGER.warn(MOD_MARKER, message, object);
    }

}
