package com.itachi1706.cheesecakeservercommands.util;

import com.itachi1706.cheesecakeservercommands.libs.selections.WorldPoint;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created by Kenneth on 2/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class WorldUtil {
    private WorldUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks if the blocks from [x,y,z] to [x,y+h-1,z] are either air or replacable
     *
     * @param world Level object
     * @param x X coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param h height of the block
     * @return y value
     */
    public static boolean isFree(Level world, int x, int y, int z, int h)
    {
        for (int i = 0; i < h; i++)
        {
            BlockState state = world.getBlockState(new BlockPos(x, y+i, z));
            if (state.getMaterial().isSolid() || state.getMaterial().isLiquid())
                return false;
        }
        return true;
    }

    /**
     * Returns a free spot of height h in the world at the coordinates [x,z] near y. If the blocks at [x,y,z] are free,
     * it returns the next location that is on the ground. If the blocks at [x,y,z] are not free, it goes up until it
     * finds a free spot.
     *
     * @param world Level object
     * @param x X coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param h height of the block
     * @return y value
     */
    public static int placeInWorld(Level world, int x, int y, int z, int h)
    {
        if (y >= 0 && isFree(world, x, y, z, h))
        {
            while (isFree(world, x, y - 1, z, h) && y > 0)
                y--;
        }
        else
        {
            if (y < 0)
                y = 0;
            y++;
            while (y + h < world.getHeight() && !isFree(world, x, y, z, h))
                y++;
        }
        if (y == 0)
            y = world.getHeight() - h;
        return y;
    }

    public static WorldPoint placeInWorld(WorldPoint p)
    {
       return p.setY(placeInWorld(p.getWorld(), p.getX(), p.getY(), p.getZ(), 2));
    }

    public static void placeInWorld(Player player)
    {
        int getY = placeInWorld(player.getLevel(), player.getBlockX(), player.getBlockY(), player.getBlockZ(), 2);

        player.setPos(player.getX() + 0.5, getY, player.getZ() + 0.5);
        WorldPoint p = placeInWorld(new WorldPoint(player));
        player.moveTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5);
    }

    /* ------------------------------------------------------------ */
}
