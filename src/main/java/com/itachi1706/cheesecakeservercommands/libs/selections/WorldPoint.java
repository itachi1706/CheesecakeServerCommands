package com.itachi1706.cheesecakeservercommands.libs.selections;

import com.google.gson.annotations.Expose;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kenneth on 2/5/2016.
 * for com.itachi1706.cheesecakeservercommands.commons.selections in CheesecakeServerCommands
 */
public class WorldPoint extends Point {
    protected String dim;

    @Expose(serialize = false)
    protected Level world;

    // ------------------------------------------------------------

    public WorldPoint(String dimension, double x, double y, double z) {
        super(x, y, z);
        dim = dimension;
    }

    public WorldPoint(Entity entity)
    {
        super(entity);
        this.dim = entity.getLevel().dimension().location().toString();
        this.world = entity.getLevel();
    }

    public String getDimension()
    {
        return dim;
    }

    @Override
    public WorldPoint setX(int x)
    {
        this.x = x;
        return this;
    }

    @Override
    public WorldPoint setY(int y)
    {
        this.y = y;
        return this;
    }

    @Override
    public WorldPoint setZ(int z)
    {
        this.z = z;
        return this;
    }

    public Level getWorld()
    {
        if (world != null && !world.dimension().location().toString().equals(dim))
            return world;
        world = ServerUtil.getServerInstance().overworld(); // Get overworld by default
        return world;
    }

    @Override
    public String toString()
    {
        return "[" + x + "," + y + "," + z + ",dim=" + dim + "]";
    }

    private static final Pattern fromStringPattern = Pattern
            .compile("\\s*\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*dim\\s*=\\s*(-?\\d+)\\s*\\]\\s*");

    public static WorldPoint fromString(String value)
    {
        Matcher m = fromStringPattern.matcher(value);
        if (m.matches())
        {
            try
            {
                return new WorldPoint(m.group(4), Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)), Double.parseDouble(m.group(3)));
            }
            catch (NumberFormatException e)
            {
                /* do nothing */
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof WorldPoint p)
        {
            return dim.equals(p.dim) && x == p.x && y == p.y && z == p.z;
        }
        if (object instanceof WarpPoint p)
        {
            return dim.equals(p.dim) && x == p.getBlockX() && y == p.getBlockY() && z == p.getBlockZ();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int h = 1 + x;
        h = h * 31 + y;
        h = h * 31 + z;
        h = h * 31 + dim.hashCode();
        return h;
    }
}
