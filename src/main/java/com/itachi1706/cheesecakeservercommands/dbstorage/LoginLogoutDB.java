package com.itachi1706.cheesecakeservercommands.dbstorage;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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
            c = DriverManager.getConnection("jdbc:sqlite:" + CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "logins.db");
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

        Statement statement;
        if (db == null){
            LogHelper.error("Unable to create table as database connection failed");
            return;
        }

        try {
            statement = db.createStatement();
            statement.executeUpdate(createDB);
            statement.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to check table exists");
        }
    }

    private static void addLog(EntityPlayerMP player, String type){
        if (player == null) return;
        if (player.connection == null) return;
        if (player.getPlayerIP() == null) return;
        String ip = player.getPlayerIP();
        String insertQuery = "INSERT INTO LOGINS (NAME,UUID,IP,X,Y,Z,WORLD,TYPE) " +
                "VALUES('" + player.getDisplayNameString() + "','" + player.getUniqueID().toString() + "','" + ip + "','"
                + (Math.round(player.posX * 100.0) / 100.0) + "','" + (Math.round(player.posY * 100.0) / 100.0) + "','"
                + (Math.round(player.posZ * 100.0) / 100.0) + "','" + player.worldObj.getWorldInfo().getWorldName() + "','"
                + type + "');";

        Connection db = getSQLiteDBConnection();
        Statement stmt;
        if (db == null){
            LogHelper.error("Unable to add log due to failed db connection");
            return;
        }

        try {
            db.setAutoCommit(false);
            stmt = db.createStatement();
            stmt.executeUpdate(insertQuery);
            stmt.close();
            db.commit();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to insert login record");
        }
    }

    public static void addLoginLog(EntityPlayer player){
        addLog((EntityPlayerMP) player, "LOGIN");
        LogHelper.info("Logged Login for " + player.getUniqueID());
    }

    public static void addLogoutLog(EntityPlayer player){
        addLog((EntityPlayerMP) player, "LOGOUT");
        LogHelper.info("Logged Logout for " + player.getUniqueID());
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
        Statement statement;
        int loginCount = 0;

        try {
            db.setAutoCommit(false);
            statement = db.createStatement();
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()){
                String tmp = rs.getString(1);
                if (tmp != null){
                    loginCount=Integer.parseInt(tmp);
                }
            }
            rs.close();
            statement.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to get " + type + " count from database");
        } catch (NumberFormatException e){
            LogHelper.error(e.toString());
            e.printStackTrace();
            return -2;
        } catch (Exception e) {
            LogHelper.error(e.toString());
            e.printStackTrace();
            return -1;
        }

        return loginCount;
    }

    public static void deleteLogs(ICommandSender iCommandSender, String target){
        String sqlQuery = "DELETE FROM LOGINS WHERE NAME='" + target + "' OR UUID='" + target + "';";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to delete logs due to failed db connection");
            return;
        }
        Statement statement;

        try{
            db.setAutoCommit(false);
            statement = db.createStatement();
            statement.executeUpdate(sqlQuery);
            db.commit();
            statement.close();
            db.close();
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.GREEN + target + " logs for login/logout deleted!");
        } catch (Exception e) {
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "An Error Occured trying to delete logs! (" + e.toString() + ")");
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

    private static void parseMessages(ArrayList<String> stringList, ICommandSender iCommandSender, int arg, String target){
        int maxPossibleValue = stringList.size();	//Max possible based on stringList
        int maxPossiblePage = (stringList.size() / 10) + 1;
        if (maxPossiblePage < arg){
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Max amount of pages is " + maxPossiblePage + ". Please specify a value within that range!");
            return;
        }
        //1 (0-9), 2 (10,19)...
        int minValue = (arg - 1) * 10;
        int maxValue = (arg * 10) - 1;
        if (maxValue > maxPossibleValue) {	//Exceeds
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "------ Login History For " + target + " Page " + arg + " of " + maxPossiblePage + " ------");
            for (int i = minValue; i < stringList.size(); i++){
                ChatHelper.sendMessage(iCommandSender, stringList.get(i));
            }
            ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "-----------------------------------------------------");
            return;
        }
        ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "------ Login History For " + target + " Page " + arg + " of " + maxPossiblePage + " ------");
        for (int i = minValue; i <= maxValue; i++){
            ChatHelper.sendMessage(iCommandSender, stringList.get(i));
        }
        ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "-----------------------------------------------------");
    }

    public static void checkLoginStats(ICommandSender p, String target, UUID uuid, String firstPlayed, String lastPlayed){
        int logins = getLoginCount(target);
        if (logins == -2){
            ChatHelper.sendMessage(p, ChatFormatting.RED + "An Error Occured trying to convert login count!");
            return;
        } else if (logins == -1){
            ChatHelper.sendMessage(p, ChatFormatting.RED + "An Error Occured trying to get login stats!");
            return;
        }
        int logouts = getLogoutCount(target);
        if (logouts == -2){
            ChatHelper.sendMessage(p, ChatFormatting.RED + "An Error Occured trying to convert logout count!");
            return;
        } else if (logouts == -1){
            ChatHelper.sendMessage(p, ChatFormatting.RED + "An Error Occured trying to get logout stats!");
            return;
        }
        String status = ChatFormatting.RED + "Offline";
        String nick = target;
        String opStatus = ChatFormatting.RED + "Not Opped";
        String gamemode = ChatFormatting.GRAY + "Unknown";
        String currentlocation = ChatFormatting.RED + "Offline";
        boolean isOnline = false;

        // Check if EntityPlayer is online
        List<EntityPlayerMP> playerEntityList = PlayerMPUtil.getOnlinePlayers();
        for (EntityPlayerMP pl : playerEntityList){
            if (pl.getDisplayNameString().equals(target) || pl.getUniqueID().equals(uuid)){
                status = ChatFormatting.GREEN + "Online";
                isOnline = true;
                nick = pl.getDisplayNameString();
                switch (pl.interactionManager.getGameType()){
                    case CREATIVE: gamemode = ChatFormatting.GREEN + "CREATIVE"; break;
                    case SURVIVAL: gamemode = ChatFormatting.GREEN + "SURVIVAL"; break;
                    case ADVENTURE: gamemode = ChatFormatting.GREEN + "ADVENTURE"; break;
                    case NOT_SET: gamemode = ChatFormatting.GRAY + "UNSET"; break;
                }

                if (PlayerMPUtil.isOperator(pl)){
                    opStatus = ChatFormatting.GREEN + "Opped";
                }

                currentlocation = pl.posX + ", " + pl.posY + ", " + pl.posZ;
                break;
            }
        }

        // Validate offline if crash or not
        if (!isOnline){
            LastKnownUsernames names = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
            if (names != null) {
                if (names.isLoginState()) status = ChatFormatting.DARK_RED + "SERVER CRASHED";
                String s = names.hasLastKnownGamemode() ? names.getLastKnownGamemode() : "";
                if (s.equals("creative")) {
                    gamemode = ChatFormatting.RED + "CREATIVE";
                } else if (s.equals("survival")) {
                    gamemode = ChatFormatting.RED + "SURVIVAL";
                } else if (s.equals("adventure")) {
                    gamemode = ChatFormatting.RED + "ADVENTURE";
                } else {
                    gamemode = ChatFormatting.GRAY + "UNSET (REQUIRES PLAYER LOGIN TO SET)";
                }
            }
        }


        //Present them all out
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "-------------------- Login Statistics -------------------");
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "Status: " + ChatFormatting.RESET + status);
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "Name: " + ChatFormatting.RESET + nick);
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "OP Status: " + ChatFormatting.RESET + opStatus);
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "UUID: " + ChatFormatting.RESET + uuid);
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "Current Gamemode: " + ChatFormatting.RESET + gamemode);
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "Login/Logout Counts: " + ChatFormatting.RESET + logins + "/" + logouts);
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "First Joined: " + ChatFormatting.RESET + firstPlayed);
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "Last Played: " + ChatFormatting.RESET + lastPlayed);
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "Current Location: " + ChatFormatting.RESET + currentlocation);
        ChatHelper.sendMessage(p, ChatFormatting.GOLD + "-----------------------------------------------------");
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
            //ChatHelper.sendMessage(p, ChatFormatting.GOLD + "--------- Login History For " + target + " ---------");
            int i = 1;
            ArrayList<String> loginHist = new ArrayList<String>();
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

    public static void checkLoginLogs(ICommandSender p, String target, int no){
        ArrayList<String> loginHist = getFullEntityPlayerLogs(target);
        if (loginHist == null){
            //Exception
            ChatHelper.sendMessage(p, ChatFormatting.RED + "An Error Occured trying to get logs!");
        } else {
            parseMessages(loginHist, p, no, target);
        }
    }
}
