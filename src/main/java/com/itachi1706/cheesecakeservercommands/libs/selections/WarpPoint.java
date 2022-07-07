package com.itachi1706.cheesecakeservercommands.libs.selections;

import com.google.gson.annotations.Expose;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.server.level.ServerLevel;

/**
 * Created by Kenneth on 2/5/2016.
 * for com.itachi1706.cheesecakeservercommands.commons.selections in CheesecakeServerCommands
 */
public class WarpPoint {
    protected String dim;

    protected float pitch;

    protected float yaw;

    protected double xd;

    protected double yd;

    protected double zd;

    @Expose(serialize = false)
    protected ServerLevel world;

    // ------------------------------------------------------------

    public WarpPoint(String dimension, double x, double y, double z, float playerPitch, float playerYaw)
    {
        this.dim = dimension;
        this.xd = x;
        this.yd = y;
        this.zd = z;
        this.pitch = playerPitch;
        this.yaw = playerYaw;
    }

    // ------------------------------------------------------------

    public String getDimension()
    {
        return dim;
    }

    public int getBlockX()
    {
        return (int) Math.floor(xd);
    }

    public int getBlockY()
    {
        return (int) Math.floor(yd);
    }

    public int getBlockZ()
    {
        return (int) Math.floor(zd);
    }

    public ServerLevel getWorld()
    {
        if (world == null || !world.dimension().location().toString().equals(dim))
            world = ServerUtil.getServerInstance().overworld(); // Default overworld
        return world;
    }

    /**
     * Returns the length of this vector
     */
    public double length()
    {
        return Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

    // ------------------------------------------------------------

    @Override
    public String toString()
    {
        return "[" + xd + "," + yd + "," + zd + ",dim=" + dim + ",pitch=" + pitch + ",yaw=" + yaw + "]";
    }

    public String toReadableString()
    {
        return String.format("%.0f %.0f %.0f dim=%d", xd, yd, zd, dim);
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof WarpPoint p)
        {
            return xd == p.xd && yd == p.yd && zd == p.zd;
        }
        if (object instanceof Point p)
        {
            return (int) xd == p.getX() && (int) yd == p.getY() && (int) zd == p.getZ();
        }
        if (object instanceof WorldPoint p)
        {
            return dim.equals(p.getDimension()) && (int) xd == p.getX() && (int) yd == p.getY() && (int) zd == p.getZ();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int h = 1 + Double.valueOf(xd).hashCode();
        h = h * 31 + Double.valueOf(yd).hashCode();
        h = h * 31 + Double.valueOf(zd).hashCode();
        h = h * 31 + Double.valueOf(pitch).hashCode();
        h = h * 31 + Double.valueOf(yaw).hashCode();
        h = h * 31 + dim.hashCode();
        return h;
    }
}
