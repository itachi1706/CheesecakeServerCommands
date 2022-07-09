package com.itachi1706.cheesecakeservercommands.commands.templates;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;

public abstract class LoggingBase extends BaseCommand {

    protected LoggingBase(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    protected void viewStats(CommandSourceStack sender, int count, String resourceLogged) {
        List<String> toSend = new ArrayList<>();
        toSend.add(ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        toSend.add(ChatFormatting.GOLD + TextUtil.centerText("General Stats"));
        toSend.add(ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        toSend.add("Total " + resourceLogged + " Logged: " + ChatFormatting.AQUA + count);
        toSend.add(ChatFormatting.GOLD + TextUtil.generateChatBreaks());

        sendSuccessMessage(sender, toSend);
    }
}
