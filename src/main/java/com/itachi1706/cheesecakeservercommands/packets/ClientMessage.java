package com.itachi1706.cheesecakeservercommands.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by Kenneth on 15/7/2018.
 * for com.itachi1706.cheesecakeservercommands.packets in CheesecakeServerCommands
 */
public class ClientMessage implements IMessage {

    // Used to send message to client
    public ClientMessage() {}

    private int state;
    private long totalDuration, currentDuration;
    private String url, fileName, songName;

    @Override
    public void fromBytes(ByteBuf buf) {
        buf.writeInt(state);
        buf.writeLong(totalDuration);
        buf.writeLong(currentDuration);
        ByteBufUtils.writeUTF8String(buf, url);
        ByteBufUtils.writeUTF8String(buf, fileName);
        ByteBufUtils.writeUTF8String(buf, songName);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        state = buf.readInt();
        totalDuration = buf.readLong();
        currentDuration = buf.readLong();
        url = ByteBufUtils.readUTF8String(buf);
        fileName = ByteBufUtils.readUTF8String(buf);
        songName = ByteBufUtils.readUTF8String(buf);
    }
}
