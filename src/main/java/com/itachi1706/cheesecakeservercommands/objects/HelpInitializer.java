package com.itachi1706.cheesecakeservercommands.objects;

import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 11/7/2016.
 * for com.itachi1706.cheesecakeservercommands.server.objects in CheesecakeServerCommands
 */
public class HelpInitializer {

    private HelpInitializer() {
        throw new IllegalStateException("Reference Class");
    }

    public static HelpMain[] initialize() {
        List<HelpMain> help = new ArrayList<>();
        help.add(new HelpMain("cheesecakelogger", "Cheesecake Logger Module Commands", initializeLogger(), true));
        help.add(new HelpMain("mojang", "Mojang Module Commands", initializeMojang()));
        help.add(new HelpMain("serverproperties", "Server Properties Module Command", initializeServerProperties(), true));
        help.add(new HelpMain("admin", "Admin Module Commands", initializeAdmin(), true));
        help.add(new HelpMain("adminitem", "Admin (Items) Module Commands", initializeAdminItem(), true));
        help.add(new HelpMain("adminworld", "Admin (World/Server) Module Commands", initializeAdminWorldServer(), true));
        help.add(new HelpMain("general", "General Module Commands", initializeGeneral()));
        help.add(new HelpMain("commanduse", "Command Logger Module Commands", initializeCommandLogger()));
        return help.toArray(new HelpMain[0]);
    }

    private static HelpSub[] initializeAdmin() {
        List<HelpSub> sub = new ArrayList<>();
        // Global
        sub.add(new HelpSub("/burn [player] [duration]", "Burns a player"));
        sub.add(new HelpSub("/fling [player]", "Flings yourself or another player into the air"));
        sub.add(new HelpSub("/fly [player]", "Enable/Disable flight for a player"));
        sub.add(new HelpSub("/feed [player]", "Feeds a player"));
        sub.add(new HelpSub("/gm <creative/adventure/survival> [player]", "Set Gamemode of Player"));
        sub.add(new HelpSub("/gma [player]", "Set Gamemode to Adventure"));
        sub.add(new HelpSub("/gmc [player]", "Set Gamemode to Creative"));
        sub.add(new HelpSub("/gms [player]", "Set Gamemode to Survival"));
        sub.add(new HelpSub("/gmsp [player]", "Set Gamemode to Spectator"));
        sub.add(new HelpSub("/god [player]", "Enable/Disable invulnerability for a player"));
        sub.add(new HelpSub("/heal [player]", "Heals a player"));
        sub.add(new HelpSub("/invsee [player]", "Views player inventory"));
        sub.add(new HelpSub("/kick <player> [reason]", "Kicks a player from the server with an optional color coded reason"));
        sub.add(new HelpSub("/kill [player] [death cause]", "Kills yourself or another player"));
        sub.add(new HelpSub("/locateplayer [player]", "Locates a player's location"));
        sub.add(new HelpSub("/smite [player/me/x] [y] [z]", "Smites a player or location"));
        sub.add(new HelpSub("/speed <fly/walk/all> <speed/reset> [player]", "Set Fly/Walk speed of player"));
        sub.add(new HelpSub("/sudo <player> <command>", "Runs a command as a user" + ChatFormatting.DARK_RED + " (VERY DANGEROUS)"));
        sub.add(new HelpSub("/tphere <player>", "Teleports another player to you"));
        sub.add(new HelpSub("/tpto <player>", "Teleports to a player"));
        sub.add(new HelpSub("/wow [player]", "Trolls yourself or another player"));
        sub.add(new HelpSub("/zeus [player]", "Let a player suffer the Wrath of Zeus"));
        return sub.toArray(new HelpSub[0]);
    }

    private static HelpSub[] initializeAdminItem() {
        List<HelpSub> sub = new ArrayList<>();
        // Item Commands
        sub.add(new HelpSub("/ci [player] [item] [damage]", "Clears player inventory of everything or a specific item"));
        sub.add(new HelpSub("/craft [player]", "Shows the crafting GUI to a player"));
        sub.add(new HelpSub("/dechant <enchantment>", "Removes all or a single enchantment from your main held item"));
        sub.add(new HelpSub("/duplicate [amount] [spillover]", "Duplicates your current main held item"));
        sub.add(new HelpSub("/enchant <enchantment> [level]", "Enchants an enchantable main held item with its enchantable enchantments"));
        sub.add(new HelpSub("/enchantforce <enchantment> [level]", "Enchant any item with any enchantments currently held in your main hand"));
        sub.add(new HelpSub("/enderchest [player]", "Opens the player's ender chest"));
        sub.add(new HelpSub("/i or /giveitem <item> [amount] [data] [player] [spillover]", "Gives items to a player"));
        sub.add(new HelpSub("/invseeender [player]", "Views a player's ender chest"));
        sub.add(new HelpSub("/more", "Max Stack your current main held item"));
        sub.add(new HelpSub("/renameitem", "Rename the item current held in your main hand"));
        sub.add(new HelpSub("/repairitem", "Restores Full Durability to the item currently held in your main hand"));
        return sub.toArray(new HelpSub[0]);
    }

    private static HelpSub[] initializeAdminWorldServer() {
        List<HelpSub> sub = new ArrayList<>();
        // World Commands
        sub.add(new HelpSub("/biomeinfo [list]", "Show current biome info or list of all available biomes"));

        // Server Commands
        sub.add(new HelpSub("/getcommandbook [player]", "Gives yourself or a player a book containing all commands on the server"));
        sub.add(new HelpSub("/modlist [page]", "List of mods on the server"));
        sub.add(new HelpSub("/serversettings <option> [value]", "Set or Get Server Settings dynamically"));
        sub.add(new HelpSub("/serverstats", "View Server Statistics (CPU/RAM)"));
        sub.add(new HelpSub("/gc", "Manually runs the JVM Garbage Collector"));
        return sub.toArray(new HelpSub[0]);
    }

    private static HelpSub[] initializeLogger() {
        List<HelpSub> sub = new ArrayList<>();
        sub.add(new HelpSub("/cclogger stats", "Gets General Statistics Logged"));
        sub.add(new HelpSub("/cclogger viewlogins <player> <#>", "View Player Login Info"));
        sub.add(new HelpSub("/cclogger viewplayerstats <player>", "View Player Stats"));
        sub.add(new HelpSub("/cclogger delloginhistory <player>", "Delete Player History"));
        sub.add(new HelpSub("/cclogger lastseen <player>", "Gets Last Seen of Player"));
        sub.add(new HelpSub("/cclogger lastknownusername <player/UUID>", "Get list of last known names of a player"));
        return sub.toArray(new HelpSub[0]);
    }

    private static HelpSub[] initializeCommandLogger() {
        List<HelpSub> sub = new ArrayList<>();
        sub.add(new HelpSub("/commanduse stats", "Gets General Command Statistics Info"));
        sub.add(new HelpSub("/commanduse viewlogs <player> <#>", "View Player Command Usage Info"));
        sub.add(new HelpSub("/commanduse viewplayerstats <player>", "View Player Command Usage Stats"));
        sub.add(new HelpSub("/commanduse dellogs <player>", "Delete Player Command Usage History"));
        sub.add(new HelpSub("/commanduse ignore <player>", "Exempts name from having its command usage logged"));
        sub.add(new HelpSub("/commanduse unignore <player>", "Unexempts name from having its command usage logged"));
        return sub.toArray(new HelpSub[0]);
    }

    private static HelpSub[] initializeServerProperties() {
        List<HelpSub> sub = new ArrayList<>();
        sub.add(new HelpSub("/serverproperties", "View Server Properties"));
        sub.add(new HelpSub("/serverstats", "View Server Statistics (CPU/RAM)"));
        sub.add(new HelpSub("/gc", "Manually runs the JVM Garbage Collector"));
        return sub.toArray(new HelpSub[0]);
    }

    private static HelpSub[] initializeMojang() {
        List<HelpSub> sub = new ArrayList<>();
        sub.add(new HelpSub("/mojang status", "View Mojang Server Status"));
        sub.add(new HelpSub("/mojang premium <name>", "Check if name is purchased"));
        return sub.toArray(new HelpSub[0]);
    }

    private static HelpSub[] initializeGeneral() {
        List<HelpSub> sub = new ArrayList<>();
        sub.add(new HelpSub("/ping", "Pings the server"));
        sub.add(new HelpSub("/csc list", "Lists out all modules in this utility"));
        sub.add(new HelpSub("/csc modulehelp", "Lists command help for specific module"));
        return sub.toArray(new HelpSub[0]);
    }

    @SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
    private static HelpSub[] sample() {
        List<HelpSub> sub = new ArrayList<>();
        return sub.toArray(new HelpSub[0]);
    }
}
