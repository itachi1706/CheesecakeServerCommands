package com.itachi1706.cheesecakeservercommands.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nullable;
import java.util.Map;

public class DechantCommand extends BaseCommand {
    public DechantCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> dechant(context.getSource(), null))
                .then(Commands.argument("enchantment", ItemEnchantmentArgument.enchantment())
                        .executes(context -> dechant(context.getSource(), ItemEnchantmentArgument.getEnchantment(context, "enchantment"))));
    }

    private int dechant(CommandSourceStack sender, @Nullable Enchantment enchantment) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot dechant for CONSOLE", "Cannot dechant for " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        ItemStack stack = player.getMainHandItem();
        if (stack == ItemStack.EMPTY) {
            sendFailureMessage(sender, "Invalid Item held");
            return 0;
        }

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.size() <= 0) {
            sendFailureMessage(sender, "No enchantments found");
            return 0;
        }

        String adminMessage;
        String chatMessage;
        if (enchantment == null) {
            enchantments.clear();
            adminMessage = "Removed all enchantments from " + stack.getDisplayName().getString();
            chatMessage = ChatFormatting.GOLD + "Removed all enchantments from " + ChatFormatting.LIGHT_PURPLE + stack.getDisplayName().getString();
        } else {
            Component comp = new TranslatableComponent(enchantment.getDescriptionId());
            if (!enchantments.containsKey(enchantment)) {
                sendFailureMessage(sender, "Unable to remove " + comp.getString() + " as it does not exist on the item");
                return 0;
            }

            enchantments.remove(enchantment);
            adminMessage = "Dechanted " + comp.getString() + " from " + stack.getDisplayName().getString();
            chatMessage = ChatFormatting.GOLD + "Removed " + ChatFormatting.AQUA + comp.getString() + ChatFormatting.GOLD +
                    " from " + ChatFormatting.LIGHT_PURPLE + stack.getDisplayName().getString();
        }

        EnchantmentHelper.setEnchantments(enchantments, stack);
        sendSuccessMessage(sender, chatMessage);
        TextUtil.sendAdminChatMessage(player, adminMessage);

        return Command.SINGLE_SUCCESS;
    }
}
