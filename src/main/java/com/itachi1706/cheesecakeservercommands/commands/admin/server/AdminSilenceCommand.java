package com.itachi1706.cheesecakeservercommands.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.nbtstorage.AdminSilenced;
import com.itachi1706.cheesecakeservercommands.nbtstorage.CSCAdminSilenceWorldSavedData;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminSilenceCommand extends BaseCommand {

    public AdminSilenceCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        final String ARG_USERNAME = "username";
        return builder
                .then(Commands.literal("list").executes(context -> listSilencedUUIDs(context.getSource())))
                .then(Commands.literal("on").executes(context -> enableAdminSilence(context.getSource())))
                .then(Commands.literal("off").executes(context -> disableAdminSilence(context.getSource())))
                .then(Commands.literal("add").then(Commands.argument(ARG_USERNAME, StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(getOnlinePlayerNames(), builder))
                        .executes(context -> attemptAddOrRemove(context.getSource(), true, context.getArgument(ARG_USERNAME, String.class)))))
                .then(Commands.literal("remove").then(Commands.argument(ARG_USERNAME, StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(getOnlinePlayerNames(), builder))
                        .executes(context -> attemptAddOrRemove(context.getSource(), false, context.getArgument(ARG_USERNAME, String.class)))));
    }
    
    private boolean notDedicatedServer(CommandSourceStack sender) {
        if (!ServerUtil.getServerInstance().isDedicatedServer()) {
            sendFailureMessage(sender, "You can only use this command on dedicated servers");
            return true;
        }
        return false;
    }

    private List<String> getOnlinePlayerNames() {
        List<String> names = new ArrayList<>();
        for (ServerPlayer player : ServerUtil.getServerPlayers().getPlayers()) {
            names.add(player.getName().getString());
        }
        return names;
    }
    
    private int listSilencedUUIDs(CommandSourceStack sender) {
        if (notDedicatedServer(sender)) return 0;

        List<UUID> uuidList = AdminSilenced.list();
        List<String> chatString = new ArrayList<>();
        chatString.add(ChatFormatting.GOLD + TextUtil.generateChatBreaks('-'));
        chatString.add("Found " + uuidList.size() + " names");
        chatString.add("Enabled: " + ((AdminSilenced.getState()) ? ChatFormatting.GREEN : ChatFormatting.RED) + AdminSilenced.getState());
        chatString.add(ChatFormatting.GOLD + TextUtil.generateChatBreaks('-'));
        for (UUID uuid : uuidList) {
            LastKnownUsernames tryPName = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
            if (tryPName == null) chatString.add(uuid.toString());
            else chatString.add(tryPName.getLastKnownUsername() + " (" + uuid.toString() + ")");
        }
        if (!uuidList.isEmpty())
            chatString.add(ChatFormatting.GOLD + TextUtil.generateChatBreaks('-'));
        sendSuccessMessage(sender, chatString);
        
        return Command.SINGLE_SUCCESS;
    }

    private CSCAdminSilenceWorldSavedData getData() {
        return CSCAdminSilenceWorldSavedData.get();
    }

    private int enableAdminSilence(CommandSourceStack sender) {
        if (notDedicatedServer(sender)) return 0;

        if (AdminSilenced.enable(getData())) {
            // Enable
            sendSuccessMessage(sender, "Enabled Admin Message Silence");
            TextUtil.sendAdminChatMessage(sender, "Enabled Admin Message Silence");
        } else
            sendFailureMessage(sender, "Admin Message Silence already enabled");
        
        return Command.SINGLE_SUCCESS;
    }
    
    private int disableAdminSilence(CommandSourceStack sender) {
        if (notDedicatedServer(sender)) return 0;

        if (AdminSilenced.disable(getData())) {
            // Enable
            sendSuccessMessage(sender, "Disabled Admin Message Silence");
            TextUtil.sendAdminChatMessage(sender, "Disabled Admin Message Silence");
        } else
            sendFailureMessage(sender, "Admin Message Silence already disabled");
        
        return Command.SINGLE_SUCCESS;
    }

    private int attemptAddOrRemove(CommandSourceStack sender, boolean toAdd, String playerNameOrUUID) {
        if (notDedicatedServer(sender)) return 0;

        UUID uuid;
        try {
            uuid = UUID.fromString(playerNameOrUUID);
        } catch (IllegalArgumentException e) {
            // Player Name
            uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerNameOrUUID);
            if (uuid == null) {
                sendFailureMessage(sender, "Failed to parse UUID from player name");
                return 0;
            }
        }
        if (toAdd) {
            if (AdminSilenced.addUUID(getData(), uuid)) {
                sendSuccessMessage(sender, String.format("Added %s to Admin Silenced List", uuid));
                TextUtil.sendAdminChatMessage(sender, String.format("Added %s to Admin Silenced List", uuid));
            } else {
                sendFailureMessage(sender, "Player/UUID already in Admin Silenced List");
            }
        } else {
            if (AdminSilenced.removeUUID(getData(), uuid)) {
                sendSuccessMessage(sender, String.format("Removed %s from Admin Silenced List", uuid));
                TextUtil.sendAdminChatMessage(sender, String.format("Removed %s from Admin Silenced List", uuid));
            } else {
                sendFailureMessage(sender, "Player/UUID is not in Admin Silenced List");
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
