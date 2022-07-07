package com.itachi1706.cheesecakeservercommands.dbstorage;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 9/12/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.dbstorage
 */
@SuppressWarnings("SqlNoDataSourceInspection")
public class LoginLogoutDB {

    public static Connection getSQLiteDBConnection(){

        Connection c;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + CheesecakeServerCommands.getConfigFileDirectory().getAbsolutePath() + File.separator + "logins.db");
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.error(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return c;
    }

    public static void checkTablesExists(){
        Connection db = getSQLiteDBConnection();
        String createDB = "CREATE TABLE IF NOT EXISTS LOGINS " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME varchar(30) not null, " +
                "UUID TEXT NOT NULL, " +
                "TIME TEXT NOT NULL DEFAULT (DATETIME('now')), " +
                "IP VARCHAR(20) NOT NULL, " +
                "X VARCHAR(30) NOT NULL, " +
                "Y VARCHAR(30) NOT NULL, " +
                "Z VARCHAR(30) NOT NULL, " +
                "WORLD TEXT NOT NULL, " +
                "TYPE VARCHAR(10) NOT NULL);";

        if (!DBUtils.createTable(db, createDB, "Login/Logout"))
            LogHelper.error("Login/Logout DB fail to ensure that table is created");
    }

    private static void addLog(ServerPlayer player, String type){
        if (player == null) return;
        String ip = player.getIpAddress();
        String insertQuery = "INSERT INTO LOGINS (NAME,UUID,IP,X,Y,Z,WORLD,TYPE) " +
                "VALUES('" + player.getDisplayName().getString() + "','" + player.getStringUUID() + "','" + ip + "','"
                + (Math.round(player.getX() * 100.0) / 100.0) + "','" + (Math.round(player.getY() * 100.0) / 100.0) + "','"
                + (Math.round(player.getZ() * 100.0) / 100.0) + "','" + player.getLevel().dimension().location() + "','"
                + type + "');";

        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to add log due to failed db connection");
            if (ServerPlayerUtil.isOperator(player) && type.equalsIgnoreCase("LOGIN")) {
                try { // Check if ODBC driver is installed if not notify them
                    Class.forName("org.sqlite.JDBC"); // Success
                } catch (ClassNotFoundException e) {
                    TextUtil.sendChatMessage(player, ChatFormatting.RED + "SQLite ODBC driver not installed. Please inform the server administrator to put it into the mods folder!"); // Fail.
                }
            }
            return;
        }

        DBUtils.insertRecord(db, insertQuery, "Unable to insert login record");
    }

    public static void addLoginLog(Player player){
        addLog((ServerPlayer) player, "LOGIN");
        LogHelper.info("Logged Login for " + player.getStringUUID());
    }

    public static void addLogoutLog(Player player){
        addLog((ServerPlayer) player, "LOGOUT");
        LogHelper.info("Logged Logout for " + player.getStringUUID());
    }

    public static int getLoginCount(String name){
        return getCount(name, "LOGIN");
    }

    public static int getLogoutCount(String name){
        return getCount(name, "LOGOUT");
    }

    private static int getCount(String name, String type){
        String queryString = "SELECT COUNT(*) FROM LOGINS WHERE (NAME='" + name + "' OR UUID='" + name + "') AND TYPE='" + type + "';";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to add log due to failed db connection");
            return -1;
        }
        int loginCount = 0;

        try {
            loginCount = DBUtils.getCount(db, queryString);
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to get " + type + " count from database");
        }

        return loginCount;
    }

    public static void deleteLogs(CommandSourceStack CommandSourceStack, String target){
        String sqlQuery = "DELETE FROM LOGINS WHERE NAME='" + target + "' OR UUID='" + target + "';";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to delete logs due to failed db connection");
            return;
        }
        try {
            DBUtils.deleteRecord(db, sqlQuery);
            TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.GREEN + target + " logs for login/logout deleted!");
        } catch (Exception e) {
            TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.RED + "An Error Occured trying to delete logs! (" + e.toString() + ")");
            LogHelper.error("Error occurred deleting logs (" + e.toString() + ")");
            e.printStackTrace();
        }
    }

    private static String sendLogin(int no, String x, String y, String z, String world, String datetime, String ip){
        //1. datetime Login at X: x, Y: y, Z:z, World: world with IP
        return (ChatFormatting.GOLD + "") + no + ". " +
                ChatFormatting.RESET + "" + ChatFormatting.ITALIC + datetime + " UTC " + ChatFormatting.RESET + "" +
                ChatFormatting.GREEN + "Login " + ChatFormatting.RESET + "at " + ChatFormatting.GOLD + "" +
                ChatFormatting.AQUA + world + ChatFormatting.GOLD + "," + ChatFormatting.AQUA + x +
                ChatFormatting.GOLD + "," + ChatFormatting.AQUA + y + ChatFormatting.GOLD + "," +
                ChatFormatting.AQUA + z + ChatFormatting.RESET + " at " + ChatFormatting.LIGHT_PURPLE + ip;
    }

    private static String sendLogout(int no, String x, String y, String z, String world, String datetime, String ip){
        //1. datetime Logout at X: x, Y: y, Z:z, World: world with IP
        return (ChatFormatting.GOLD + "") + no + ". " +
                ChatFormatting.RESET + "" + ChatFormatting.ITALIC + datetime + " UTC " + ChatFormatting.RESET + "" +
                ChatFormatting.RED + "Logout " + ChatFormatting.RESET + "at " + ChatFormatting.GOLD + "" +
                ChatFormatting.AQUA + world + ChatFormatting.GOLD + "," + ChatFormatting.AQUA + x +
                ChatFormatting.GOLD + "," + ChatFormatting.AQUA + y + ChatFormatting.GOLD + "," +
                ChatFormatting.AQUA + z + ChatFormatting.RESET + " at " + ChatFormatting.LIGHT_PURPLE + ip;
    }

    private static void parseMessages(ArrayList<String> stringList, CommandSourceStack CommandSourceStack, int arg, String target){
        int maxPossibleValue = stringList.size();	//Max possible based on stringList
        int maxPossiblePage = (stringList.size() / 10) + 1;
        if (maxPossiblePage < arg){
            TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.RED + "Max amount of pages is " + maxPossiblePage + ". Please specify a value within that range!");
            return;
        }
        //1 (0-9), 2 (10,19)...
        int minValue = (arg - 1) * 10;
        int maxValue = (arg * 10) - 1;
        if (maxValue > maxPossibleValue) {	//Exceeds
            TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.GOLD + TextUtil.centerText(" Login History For " + target + " Page " + arg + " of " + maxPossiblePage + " ", '-'));
            for (int i = minValue; i < stringList.size(); i++){
                TextUtil.sendChatMessage(CommandSourceStack, stringList.get(i));
            }
            TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.GOLD + TextUtil.generateChatBreaks('-'));
            return;
        }
        TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.GOLD + TextUtil.centerText(" Login History For " + target + " Page " + arg + " of " + maxPossiblePage + " ", '-'));
        for (int i = minValue; i <= maxValue; i++){
            TextUtil.sendChatMessage(CommandSourceStack, stringList.get(i));
        }
        TextUtil.sendChatMessage(CommandSourceStack, ChatFormatting.GOLD + TextUtil.generateChatBreaks('-'));
    }

    public static void checkLoginStats(CommandSourceStack p, String target, UUID uuid, String firstPlayed, String lastPlayed){
        int logins = getLoginCount(target);
        if (logins == -2){
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occured trying to convert login count!");
            return;
        } else if (logins == -1){
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occured trying to get login stats!");
            return;
        }
        int logouts = getLogoutCount(target);
        if (logouts == -2){
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occured trying to convert logout count!");
            return;
        } else if (logouts == -1){
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occured trying to get logout stats!");
            return;
        }
        String status = ChatFormatting.RED + "Offline";
        String nick = target;
        String opStatus = ChatFormatting.RED + "Not Opped";
        String gamemode = ChatFormatting.GRAY + "Unknown";
        String currentlocation = ChatFormatting.RED + "Offline";
        boolean isOnline = false;

        // Check if EntityPlayer is online
        List<ServerPlayer> playerEntityList = ServerPlayerUtil.getOnlinePlayers();
        for (ServerPlayer pl : playerEntityList){
            if (pl.getDisplayName().getString().equals(target) || pl.getUUID().equals(uuid)){
                status = ChatFormatting.GREEN + "Online";
                isOnline = true;
                nick = pl.getDisplayName().getString();
                switch (pl.gameMode.getGameModeForPlayer()){
                    case CREATIVE: gamemode = ChatFormatting.GREEN + "CREATIVE"; break;
                    case SURVIVAL: gamemode = ChatFormatting.GREEN + "SURVIVAL"; break;
                    case ADVENTURE: gamemode = ChatFormatting.GREEN + "ADVENTURE"; break;
                    case SPECTATOR: gamemode = ChatFormatting.GREEN + "SPECTATOR"; break;
                    default: gamemode = ChatFormatting.GRAY + "UNSET"; break;
                }

                if (ServerPlayerUtil.isOperator(pl)){
                    opStatus = ChatFormatting.GREEN + "Opped";
                }

                currentlocation = pl.getX() + ", " + pl.getY() + ", " + pl.getZ();
                break;
            }
        }

        // Validate offline if crash or not
        if (!isOnline){
            LastKnownUsernames names = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
            if (names != null) {
                if (names.isLoginState()) status = ChatFormatting.DARK_RED + "SERVER CRASHED";
                String s = names.hasLastKnownGamemode() ? names.getLastKnownGamemode() : "";
                switch (s) {
                    case "creative":
                        gamemode = ChatFormatting.RED + "CREATIVE";
                        break;
                    case "survival":
                        gamemode = ChatFormatting.RED + "SURVIVAL";
                        break;
                    case "adventure":
                        gamemode = ChatFormatting.RED + "ADVENTURE";
                        break;
                    default:
                        gamemode = ChatFormatting.GRAY + "UNSET (REQUIRES PLAYER LOGIN TO SET)";
                        break;
                }
            }
        }


        //Present them all out
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + TextUtil.centerText(" Login Statistics ", '-'));
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "Status: " + ChatFormatting.RESET + status);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "Name: " + ChatFormatting.RESET + nick);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "OP Status: " + ChatFormatting.RESET + opStatus);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "UUID: " + ChatFormatting.RESET + uuid);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "Current Gamemode: " + ChatFormatting.RESET + gamemode);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "Login/Logout Counts: " + ChatFormatting.RESET + logins + "/" + logouts);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "First Joined: " + ChatFormatting.RESET + firstPlayed);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "Last Played: " + ChatFormatting.RESET + lastPlayed);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + "Current Location: " + ChatFormatting.RESET + currentlocation);
        TextUtil.sendChatMessage(p, ChatFormatting.GOLD + TextUtil.generateChatBreaks('-'));
    }

    private static ArrayList<String> getFullEntityPlayerLogs(String target){
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to check login stats due to failed db connection");
            return null;
        }
        Statement statement;

        String querySQL = "SELECT NAME,UUID,TYPE,X,Y,Z,WORLD,TIME,IP FROM LOGINS WHERE (NAME='" + target + "' OR UUID='" + target + "') ORDER BY TIME DESC;";

        try {
            db.setAutoCommit(false);
            statement = db.createStatement();
            ResultSet rs = statement.executeQuery(querySQL);
            int i = 1;
            ArrayList<String> loginHist = new ArrayList<>();
            while (rs.next()){
                if (rs.getString("TYPE").equalsIgnoreCase("LOGIN")){
                    //A login message
                    loginHist.add(sendLogin(i, rs.getString("X"), rs.getString("Y"), rs.getString("Z"), rs.getString("WORLD"), rs.getString("TIME"), rs.getString("IP")));
                } else if (rs.getString("TYPE").equalsIgnoreCase("LOGOUT")){
                    //A logout message
                    loginHist.add(sendLogout(i, rs.getString("X"), rs.getString("Y"), rs.getString("Z"), rs.getString("WORLD"), rs.getString("TIME"), rs.getString("IP")));
                }
                i++;
            }
            rs.close();
            statement.close();
            db.close();
            return loginHist;
        } catch (Exception e) {
            LogHelper.error("Error Occurred parsing player logs (" + e.toString() + ")");
            e.printStackTrace();
            return null;
        }
    }

    public static void checkLoginLogs(CommandSourceStack p, String target, int no){
        ArrayList<String> loginHist = getFullEntityPlayerLogs(target);
        if (loginHist == null){
            //Exception
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occured trying to get logs!");
        } else {
            parseMessages(loginHist, p, no, target);
        }
    }
}
