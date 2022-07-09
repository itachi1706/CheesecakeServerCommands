package com.itachi1706.cheesecakeservercommands.libs.selections;

import net.minecraft.world.entity.Entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kenneth on 2/5/2016.
 * for com.itachi1706.cheesecakeservercommands.commons.selections in CheesecakeServerCommands
 */
public class Point {

    protected int x;

    protected int y;

    protected int z;

    // ------------------------------------------------------------

    public Point(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point (double x, double y, double z)
    {
        this.x = ((int) x);
        this.y = ((int) y);
        this.z = ((int) z);
    }

    public Point(Entity entity)
    {
        x = (int) Math.floor(entity.position().x);
        y = (int) Math.floor(entity.position().y);
        z = (int) Math.floor(entity.position().z);
    }

    // ------------------------------------------------------------

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public Point setX(int x)
    {
        this.x = x;
        return this;
    }

    public Point setY(int y)
    {
        this.y = y;
        return this;
    }

    public Point setZ(int z)
    {
        this.z = z;
        return this;
    }

    // ------------------------------------------------------------

    /**
     * Returns the length of this vector
     */
    public double length()
    {
        return Math.sqrt((double)x * x + y * y + z * z);
    }

    public void add(Point v)
    {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    // ------------------------------------------------------------

    @Override
    public String toString()
    {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    private static final Pattern pattern = Pattern.compile("\\s*\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*]\\s*");

    public static Point fromString(String value)
    {
        Matcher match = pattern.matcher(value);
        if (!match.matches())
            return null;
        return new Point(Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2)), Integer.parseInt(match.group(3)));
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Point p)
        {
            return x == p.x && y == p.y && z == p.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1 + x;
        h = h * 31 + y;
        h = h * 31 + z;
        return h;
    }

}