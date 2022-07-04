package com.itachi1706.cheesecakeservercommands.commons.selections;

import com.google.gson.annotations.Expose;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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

    public WorldPoint(String dimension, double x, double y, double z)
    {
        super(x, y, z);
        dim = dimension;
    }

    public WorldPoint(String dimension, HitResult location)
    {
        this(dimension, location.getLocation().x, location.getLocation().y, location.getLocation().z);
    }

    public WorldPoint(ServerLevel world, double x, double y, double z)
    {
        super(x, y, z);
        this.dim = world.dimension().location().toString();
        this.world = world;
    }

    public WorldPoint(ServerLevel world, HitResult location)
    {
        this(world, location.getLocation().x, location.getLocation().y, location.getLocation().z);
    }

    // TODO: Figure out how to convert this
    /*public WorldPoint(World world, ChunkPosition location)
    {
        ChunkPos locate;
        this(world, location.chunkPosX, location.chunkPosY, location.chunkPosZ);
    }*/

    public WorldPoint(Entity entity)
    {
        super(entity);
        this.dim = entity.getLevel().dimension().location().toString();
        this.world = entity.getLevel();
    }

    public WorldPoint(String dim, Vec3 vector)
    {
        super(vector);
        this.dim = dim;
    }

    public WorldPoint(WorldPoint other)
    {
        this(other.dim, other.x, other.y, other.z);
    }

    public WorldPoint(String dimension, Point point)
    {
        this(dimension, point.x, point.y, point.z);
    }

    public WorldPoint(WarpPoint other)
    {
        this(other.getDimension(), other.getBlockX(), other.getBlockY(), other.getBlockZ());
    }

    // TODO: Figure out how to get Level from this
    // public WorldPoint(BlockEvent event)
    // {
    //     this(event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
    // }

    // TODO: Figure out how to convert this
    /*public static WorldPoint create(ICommandSender sender)
    {
        return new WorldPoint(sender.getEntityWorld(), sender.getPlayerCoordinates());
    }*/

    // ------------------------------------------------------------

    public String getDimension()
    {
        return dim;
    }

    public void setDimension(String dim)
    {
        this.dim = dim;
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

    public WarpPoint toWarpPoint(float rotationPitch, float rotationYaw)
    {
        return new WarpPoint(this, rotationPitch, rotationYaw);
    }

    public Block getBlock()
    {
        return getWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    // TODO: Figure out how to convert this
    /*public int getBlockMeta()
    {
        return getWorld().getBlockMetadata(x, y, z);
    }*/

    // TODO: Figure out how to convert this
    /*public TileEntity getTileEntity()
    {
        return getWorld().getTileEntity(x, y, z);
    }*/

    // ------------------------------------------------------------

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
        if (object instanceof WorldPoint)
        {
            WorldPoint p = (WorldPoint) object;
            return dim == p.dim && x == p.x && y == p.y && z == p.z;
        }
        if (object instanceof WarpPoint)
        {
            WarpPoint p = (WarpPoint) object;
            return dim == p.dim && x == p.getBlockX() && y == p.getBlockY() && z == p.getBlockZ();
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
