package com.itachi1706.cheesecakeservercommands.commons.selections;

import com.google.gson.annotations.Expose;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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

    public WarpPoint(ServerLevel world, double x, double y, double z, float playerPitch, float playerYaw)
    {
        this.world = world;
        this.dim = world.dimension().location().toString();
        this.xd = x;
        this.yd = y;
        this.zd = z;
        this.pitch = playerPitch;
        this.yaw = playerYaw;
    }

    public WarpPoint(String dimension, HitResult location, float pitch, float yaw)
    {
        this(dimension, location.getLocation().x + 0.5, location.getLocation().y, location.getLocation().z + 0.5, pitch, yaw);
    }

    public WarpPoint(Point point, String dimension, float pitch, float yaw)
    {
        this(dimension, point.getX(), point.getY(), point.getZ(), pitch, yaw);
    }

    public WarpPoint(WorldPoint point, float pitch, float yaw)
    {
        this(point.getDimension(), point.getX() + 0.5, point.getY(), point.getZ() + 0.5, pitch, yaw);
    }

    public WarpPoint(WorldPoint point)
    {
        this(point, 0, 0);
    }

    public WarpPoint(Entity entity)
    {
        this(entity.getLevel() instanceof ServerLevel ? (ServerLevel) entity.getLevel() : null, entity.position().x, entity.position().y, entity.position().z, entity.getXRot(), entity.getYRot());
    }

    public WarpPoint(WarpPoint point)
    {
        this(point.dim, point.xd, point.yd, point.zd, point.pitch, point.yaw);
    }

    // ------------------------------------------------------------

    public String getDimension()
    {
        return dim;
    }

    public double getX()
    {
        return xd;
    }

    public double getY()
    {
        return yd;
    }

    public double getZ()
    {
        return zd;
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

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }

    public void setDimension(String dim)
    {
        this.dim = dim;
    }

    public ServerLevel getWorld()
    {
        if (world == null || !world.dimension().location().toString().equals(dim))
            world = ServerUtil.getServerInstance().overworld(); // Default overworld
        return world;
    }

    public void setX(double value)
    {
        xd = value;
    }

    public void setY(double value)
    {
        yd = value;
    }

    public void setZ(double value)
    {
        zd = value;
    }

    public void setPitch(float value)
    {
        pitch = value;
    }

    public void setYaw(float value)
    {
        yaw = value;
    }

    // ------------------------------------------------------------

    /**
     * Returns the length of this vector
     */
    public double length()
    {
        return Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

    /**
     * Returns the distance to another point
     */
    public double distance(WarpPoint v)
    {
        return Math.sqrt((xd - v.xd) * (xd - v.xd) + (yd - v.yd) * (yd - v.yd) + (zd - v.zd) * (zd - v.zd));
    }

    /**
     * Returns the distance to another entity
     */
    public double distance(Entity e)
    {
        return Math.sqrt((xd - e.position().x) * (xd - e.position().x) + (yd - e.position().y) * (yd - e.position().y) + (zd - e.position().z) * (zd - e.position().z));
    }

    public void validatePositiveY()
    {
        if (yd < 0)
            yd = 0;
    }

    public Vec3 toVec3()
    {
        return new Vec3(xd, yd, zd);
    }

    public WorldPoint toWorldPoint()
    {
        return new WorldPoint(this);
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
        if (object instanceof WarpPoint)
        {
            WarpPoint p = (WarpPoint) object;
            return xd == p.xd && yd == p.yd && zd == p.zd;
        }
        if (object instanceof Point)
        {
            Point p = (Point) object;
            return (int) xd == p.getX() && (int) yd == p.getY() && (int) zd == p.getZ();
        }
        if (object instanceof WorldPoint)
        {
            WorldPoint p = (WorldPoint) object;
            return dim == p.getDimension() && (int) xd == p.getX() && (int) yd == p.getY() && (int) zd == p.getZ();
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
