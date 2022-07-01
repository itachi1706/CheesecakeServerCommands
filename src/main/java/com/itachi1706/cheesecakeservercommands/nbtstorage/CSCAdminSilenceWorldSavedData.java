package com.itachi1706.cheesecakeservercommands.nbtstorage;

import com.itachi1706.cheesecakeservercommands.reference.References;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Created by Kenneth on 1/7/2018.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class CSCAdminSilenceWorldSavedData extends SavedData {

    private static final String DATA_NAME = References.MOD_ID_SHORT + "_AdminSilenceData";

    private static final String SILENCE_LIST = "AdminSilencedList";
    private static final String SILENCE_ENABLED = "AdminSilencedEnabled";
    private static final String CMDUSE_IGNORE_LIST = "CommandUseList";

    public CSCAdminSilenceWorldSavedData() {

    }

    public static CSCAdminSilenceWorldSavedData create() {
        return new CSCAdminSilenceWorldSavedData();
    }

    public static CSCAdminSilenceWorldSavedData readFromNBT(CompoundTag nbt) {
        AdminSilenced.reinit();
        ListTag list = nbt.getList(SILENCE_LIST, Tag.TAG_STRING);
        for (int i=0;i < list.size();i++) {
            String s = list.getString(i);
            UUID uuid = UUID.fromString(s);
            AdminSilenced.silencedList.add(uuid);
        }
        ListTag cmdlist = nbt.getList(CMDUSE_IGNORE_LIST, Tag.TAG_STRING);
        for (int i=0;i < cmdlist.size();i++) {
            String s = cmdlist.getString(i);
            AdminSilenced.ignoredCommandUser.add(s);
        }
        AdminSilenced.enabled = nbt.getBoolean(SILENCE_ENABLED);
        LogHelper.info("Read from Admin Silence NBT");

        return create();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        ListTag list = new ListTag();
        for (UUID uuid : AdminSilenced.silencedList) {
            StringTag s = StringTag.valueOf(uuid.toString());
            list.add(s);
        }
        ListTag cmdList = new ListTag();
        for (String s : AdminSilenced.ignoredCommandUser) {
            StringTag str = StringTag.valueOf(s);
            cmdList.add(str);
        }
        nbt.put(SILENCE_LIST, list);
        nbt.put(CMDUSE_IGNORE_LIST, cmdList);
        nbt.putBoolean(SILENCE_ENABLED, AdminSilenced.enabled);
        LogHelper.info("Wrote to Admin Silence NBT");
        return nbt;
    }

    /**
     * Retired use {@link CSCAdminSilenceWorldSavedData#get()} instead
     * @param world Server World
     * @param global Is Global
     * @return Saved Data
     */
    @Deprecated
    public static CSCAdminSilenceWorldSavedData get(ServerLevel world, boolean global) {
        // The IS_GLOBAL constant is there for clarity, and should be simplified into the right branch.
        return get();
    }

    public static CSCAdminSilenceWorldSavedData get() {
        DimensionDataStorage storage = ServerUtil.getServerInstance().overworld().getDataStorage();
        return storage.computeIfAbsent(CSCAdminSilenceWorldSavedData::readFromNBT, CSCAdminSilenceWorldSavedData::create, DATA_NAME);
    }
}
