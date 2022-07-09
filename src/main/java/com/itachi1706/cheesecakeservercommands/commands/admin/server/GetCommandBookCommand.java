package com.itachi1706.cheesecakeservercommands.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Set;
import java.util.TreeSet;

public class GetCommandBookCommand extends BaseCommand {

    public GetCommandBookCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> giveBook(context.getSource()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> giveBook(context.getSource(), EntityArgument.getPlayer(context, "player"), false)));
    }

    private int giveBook(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot give command book to CONSOLE", "An internal server error occurred");
        if (player == null) {
            return 0; // Already sent message
        }

        return giveBook(sender, player, true);
    }

    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHOR = "author";

    private int giveBook(CommandSourceStack sender, ServerPlayer player, boolean giveToSelf) {
        if (player.getInventory().contains(new ItemStack(Items.WRITTEN_BOOK))) {
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack stack = player.getInventory().items.get(i);
                if (checkIfHasItemAndIsBookWithTag(stack) && checkTitle(stack) && checkAuthor(stack)) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }

        Set<String> pages = new TreeSet<>();
        for (CommandNode<CommandSourceStack> command : ServerUtil.getServerInstance().getCommands().getDispatcher().getRoot().getChildren()) {
            String commandUsage = command.getUsageText();
            String text = ChatFormatting.GOLD + "/" + command.getName() + "\n" + ChatFormatting.BLACK + commandUsage;
            pages.add(text);
        }

        ListTag pagesNbt = new ListTag();
        for (String page : pages)
            pagesNbt.add(StringTag.valueOf("{\"text\":\"" + page + "\"}"));


        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_AUTHOR, "Cheesecake Network");
        tag.putString(TAG_TITLE, "CommandBook");
        tag.put("pages", pagesNbt);

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        is.setTag(tag);
        player.getInventory().add(is);

        if (giveToSelf) {
            sendSuccessMessage(sender, ChatFormatting.GOLD + "Obtained a Command Book");
            TextUtil.sendAdminChatMessage(sender, "Obtained a Command Book");
        } else {
            sendSuccessMessage(sender, ChatFormatting.GOLD + "Gave a Command Book to " + player.getName());
            TextUtil.sendAdminChatMessage(sender, "Gave a Command Book to " + player.getName());
            sendMessage(player, ChatFormatting.GOLD + "Received a Command Book");
        }

        return Command.SINGLE_SUCCESS;
    }

    // Checks
    private boolean checkIfHasItemAndIsBookWithTag(ItemStack stack) {
        return !stack.equals(ItemStack.EMPTY) && stack.getItem() == Items.WRITTEN_BOOK && stack.hasTag() && stack.getTag() != null;
    }

    private boolean checkTitle(ItemStack stack) {
        assert stack.getTag() != null; // Checked in previous method
        return stack.getTag().contains(TAG_TITLE) && stack.getTag().getString(TAG_TITLE).equals("CommandBook");
    }

    private boolean checkAuthor(ItemStack stack) {
        assert stack.getTag() != null; // Checked in previous method
        return stack.getTag().contains(TAG_AUTHOR) && stack.getTag().getString(TAG_AUTHOR).equals("Cheesecake Network");
    }
}
