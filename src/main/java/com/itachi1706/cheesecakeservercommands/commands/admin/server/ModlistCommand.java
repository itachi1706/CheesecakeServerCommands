package com.itachi1706.cheesecakeservercommands.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.Locale;

public class ModlistCommand extends BaseCommand {

    private static final int MODS_PER_PAGE = 7;

    public ModlistCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> viewModList(context.getSource(), 1))
                .then(Commands.argument("page", IntegerArgumentType.integer(1, getPages()))
                        .executes(context -> viewModList(context.getSource(), IntegerArgumentType.getInteger(context, "page"))));
    }

    private int getPages() {
        int size = getModsListSize();
        return (int) Math.ceil(size / (float) MODS_PER_PAGE);
    }

    private int getModsListSize() {
        return ModList.get().size();
    }

    private List<String> getModList() {
        return ModList.get().applyForEachModContainer(modContainer -> String.format(Locale.ROOT, "%s (%s) - %s%s",
                modContainer.getModInfo().getDisplayName(),
                modContainer.getModInfo().getModId(),
                ChatFormatting.AQUA,
                modContainer.getModInfo().getVersion().getQualifier())).toList();
    }

    private int viewModList(CommandSourceStack sender, int page) {

        int pages = getPages();
        page -= 1; // Make sure page is 1-indexed

        int min = Math.min(page * MODS_PER_PAGE, getModsListSize());

        if (page >= pages) {
            sendFailureMessage(sender, "Invalid Page. There are only " + pages + " pages");
            return 0;
        }

        String formatMessage = String.format(TextUtil.centerText(" Showing modlist page %1$d of %2$d ", '-'), page + 1, pages);
        sendSuccessMessage(sender, ChatFormatting.GOLD + formatMessage);
        List<String> modList = getModList();
        for (int i = page * MODS_PER_PAGE; i < min + MODS_PER_PAGE; i++)
        {
            if (i >= getModsListSize())
            {
                break;
            }
            sendSuccessMessage(sender, modList.get(i));
        }
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks('-'));
        return Command.SINGLE_SUCCESS;
    }
}
