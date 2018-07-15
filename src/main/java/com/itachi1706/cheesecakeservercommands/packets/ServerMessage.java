package com.itachi1706.cheesecakeservercommands.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by Kenneth on 15/7/2018.
 * for com.itachi1706.cheesecakeservercommands.packets in CheesecakeServerCommands
 */
public class ServerMessage implements IMessage {

    // Used to send message to server
    public ServerMessage() {}

    private int request;

    public ServerMessage(int request) {
        this.request = request;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buf.writeInt(request);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        request = buf.readInt();
    }
}
