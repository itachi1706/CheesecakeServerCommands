package com.itachi1706.cheesecakeservercommands.nbtstorage;

import com.itachi1706.cheesecakeservercommands.reference.References;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

/**
 * Created by Kenneth on 1/7/2018.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class CSCAdminSilenceWorldSavedData extends WorldSavedData {

    private static final String DATA_NAME = References.MOD_ID_SHORT + "_AdminSilenceData";

    private static final String SILENCE_LIST = "AdminSilencedList";
    private static final String SILENCE_ENABLED = "AdminSilencedEnabled";

    public CSCAdminSilenceWorldSavedData() {
        super(DATA_NAME);
    }

    public CSCAdminSilenceWorldSavedData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        AdminSilenced.reinit();
        NBTTagList list = nbt.getTagList(SILENCE_LIST, Constants.NBT.TAG_STRING);
        for (int i=0;i < list.tagCount();i++) {
            String s = list.getStringTagAt(i);
            UUID uuid = UUID.fromString(s);
            AdminSilenced.silencedList.add(uuid);
        }
        AdminSilenced.enabled = nbt.getBoolean(SILENCE_ENABLED);
        LogHelper.info("Read from Admin Silence NBT");
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (UUID uuid : AdminSilenced.silencedList) {
            NBTTagString s = new NBTTagString(uuid.toString());
            list.appendTag(s);
        }
        nbt.setTag(SILENCE_LIST, list);
        nbt.setBoolean(SILENCE_ENABLED, AdminSilenced.enabled);
        LogHelper.info("Wrote to Admin Silence NBT");
        return nbt;
    }

    public static CSCAdminSilenceWorldSavedData get(World world, boolean global) {
        // The IS_GLOBAL constant is there for clarity, and should be simplified into the right branch.
        MapStorage storage = global ? world.getMapStorage() : world.getPerWorldStorage();
        CSCAdminSilenceWorldSavedData instance = (CSCAdminSilenceWorldSavedData) storage.getOrLoadData(CSCAdminSilenceWorldSavedData.class, DATA_NAME);

        if (instance == null) {
            instance = new CSCAdminSilenceWorldSavedData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }
}
