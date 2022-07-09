package com.itachi1706.cheesecakeservercommands.commands.admin.item;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class EnchantCommand extends BaseCommand {
    private static final String ARG_ENCHANTMENT = "enchantment";

    public EnchantCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.argument(ARG_ENCHANTMENT, ItemEnchantmentArgument.enchantment())
                        .executes(context -> enchantItem(context.getSource(), ItemEnchantmentArgument.getEnchantment(context, ARG_ENCHANTMENT), -1))
                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                .executes(context -> enchantItem(context.getSource(), ItemEnchantmentArgument.getEnchantment(context, ARG_ENCHANTMENT),
                                        IntegerArgumentType.getInteger(context, "level")))));
    }

    private int enchantItem(CommandSourceStack sender, Enchantment enchantment, int level) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot enchant item for CONSOLE", "Cannot enchant item for " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        ItemStack item = player.getMainHandItem();
        if (item == ItemStack.EMPTY) {
            sendFailureMessage(sender, "Invalid item held");
            return 0;
        }

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);

        if (level == -1) {
            level = enchantment.getMaxLevel();
        }

        enchantments.put(enchantment, level);

        EnchantmentHelper.setEnchantments(enchantments, item);

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Enchanted " + ChatFormatting.LIGHT_PURPLE + item.getDisplayName().getString() + ChatFormatting.GOLD +
                " with " + ChatFormatting.AQUA + enchantment.getFullname(level).getString());
        TextUtil.sendAdminChatMessage(sender, "Enchanted " + item.getDisplayName().getString() + " with " + enchantment.getFullname(level).getString());

        return Command.SINGLE_SUCCESS;
    }
}
