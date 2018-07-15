package com.itachi1706.cheesecakeservercommands.packets;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Kenneth on 15/7/2018.
 * for com.itachi1706.cheesecakeservercommands.packets in CheesecakeServerCommands
 */
public class ServerMessageHandler implements IMessageHandler<ServerMessage, IMessage> {
    @Override
    public IMessage onMessage(ServerMessage message, MessageContext ctx) {
        return null;
    }
}
