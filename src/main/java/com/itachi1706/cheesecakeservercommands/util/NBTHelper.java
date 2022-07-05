package com.itachi1706.cheesecakeservercommands.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
public class NBTHelper {

    private static final String TAG_STATE_EXCEPTION = "Tag should not be null anymore";
    
    private NBTHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean hasTag(ItemStack itemStack, String keyName)
    {
        return itemStack != null && itemStack.getTag() != null && itemStack.getTag().contains(keyName);
    }

    public static void removeTag(ItemStack itemStack, String keyName)
    {
        if (itemStack.getTag() != null)
        {
            itemStack.getTag().remove(keyName);
        }
    }

    /**
     * Initializes the NBT Tag Compound for the given ItemStack if it is null
     *
     * @param itemStack
     *         The ItemStack for which its NBT Tag Compound is being checked for initialization
     */
    private static void initNBTTagCompound(ItemStack itemStack)
    {
        if (itemStack.getTag() == null)
        {
            itemStack.setTag(new CompoundTag());
        }
    }

    public static void setLong(ItemStack itemStack, String keyName, long keyValue)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        itemStack.getTag().putLong(keyName, keyValue);
    }

    // String
    public static String getString(ItemStack itemStack, String keyName)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        if (!itemStack.getTag().contains(keyName))
        {
            setString(itemStack, keyName, "");
        }

        return itemStack.getTag().getString(keyName);
    }

    public static void setString(ItemStack itemStack, String keyName, String keyValue)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        itemStack.getTag().putString(keyName, keyValue);
    }

    // boolean
    public static boolean getBoolean(ItemStack itemStack, String keyName)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        if (!itemStack.getTag().contains(keyName))
        {
            setBoolean(itemStack, keyName, false);
        }

        return itemStack.getTag().getBoolean(keyName);
    }

    public static void setBoolean(ItemStack itemStack, String keyName, boolean keyValue)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        itemStack.getTag().putBoolean(keyName, keyValue);
    }

    // byte
    public static byte getByte(ItemStack itemStack, String keyName)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        if (!itemStack.getTag().contains(keyName))
        {
            setByte(itemStack, keyName, (byte) 0);
        }

        return itemStack.getTag().getByte(keyName);
    }

    public static void setByte(ItemStack itemStack, String keyName, byte keyValue)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        itemStack.getTag().putByte(keyName, keyValue);
    }

    // short
    public static short getShort(ItemStack itemStack, String keyName)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        if (!itemStack.getTag().contains(keyName))
        {
            setShort(itemStack, keyName, (short) 0);
        }

        return itemStack.getTag().getShort(keyName);
    }

    public static void setShort(ItemStack itemStack, String keyName, short keyValue)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        itemStack.getTag().putShort(keyName, keyValue);
    }

    // int
    public static int getInt(ItemStack itemStack, String keyName)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        if (!itemStack.getTag().contains(keyName))
        {
            setInteger(itemStack, keyName, 0);
        }

        return itemStack.getTag().getInt(keyName);
    }

    public static void setInteger(ItemStack itemStack, String keyName, int keyValue)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        itemStack.getTag().putInt(keyName, keyValue);
    }

    // long
    public static long getLong(ItemStack itemStack, String keyName)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        if (!itemStack.getTag().contains(keyName))
        {
            setLong(itemStack, keyName, 0);
        }

        return itemStack.getTag().getLong(keyName);
    }

    // float
    public static float getFloat(ItemStack itemStack, String keyName)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        if (!itemStack.getTag().contains(keyName))
        {
            setFloat(itemStack, keyName, 0);
        }

        return itemStack.getTag().getFloat(keyName);
    }

    public static void setFloat(ItemStack itemStack, String keyName, float keyValue)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        itemStack.getTag().putFloat(keyName, keyValue);
    }

    // double
    public static double getDouble(ItemStack itemStack, String keyName)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }
        
        if (!itemStack.getTag().contains(keyName))
        {
            setDouble(itemStack, keyName, 0);
        }

        return itemStack.getTag().getDouble(keyName);
    }

    public static void setDouble(ItemStack itemStack, String keyName, double keyValue)
    {
        initNBTTagCompound(itemStack);
        if (itemStack.getTag() == null)
        {
            throw new IllegalStateException(TAG_STATE_EXCEPTION);
        }

        itemStack.getTag().putDouble(keyName, keyValue);
    }

}
