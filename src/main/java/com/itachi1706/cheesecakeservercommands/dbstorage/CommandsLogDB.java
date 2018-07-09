package com.itachi1706.cheesecakeservercommands.dbstorage;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Kenneth on 9/7/2018.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.dbstorage
 */
@SuppressWarnings("SqlNoDataSourceInspection")
public class CommandsLogDB {

    private static Connection getSQLiteDBConnection(){
        Connection c;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + CheesecakeServerCommands.configFileDirectory.getAbsolutePath() + File.separator + "command_use.db");
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.error(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return c;
    }

    public static void checkTablesExists(){
        Connection db = getSQLiteDBConnection();
        String createDB = "CREATE TABLE IF NOT EXISTS COMMANDS " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME varchar(30) not null, " +
                "UUID TEXT NOT NULL, " +
                "TIME TEXT NOT NULL DEFAULT (DATETIME('now')), " +
                "IP VARCHAR(20) NOT NULL, " +
                "COMMAND_BASE TEXT NOT NULL, " +
                "FULL_COMMAND TEXT NOT NULL);";

        if (!DBUtils.createTable(db, createDB, "Commands"))
            LogHelper.error("Commands Log DB fail to ensure that table is created");
    }

    public static void addLog(String commandUser, UUID uuid, String commandBase, String[] commandArgs, String ip) {
        StringBuilder commandExecuted = new StringBuilder(commandBase + " ");
        for (String s : commandArgs) {
            commandExecuted.append(s).append(" ");
        }
        String insertQuery = "INSERT INTO COMMANDS (NAME,UUID,IP,COMMAND_BASE,FULL_COMMAND) " +
                "VALUES('" + commandUser + "','" + uuid + "','" + ip + "','"
                + commandBase + "','" + commandExecuted.toString().trim() + "');";

        Connection db = getSQLiteDBConnection();
        if (db == null) {
            LogHelper.error("Unable to add log due to failed db connection");
            return;
        }

        DBUtils.insertRecord(db, insertQuery, "Unable to insert command record");
    }

    private static int getCommandUsageByPlayerNameOrUuid(String name){
        return getCount(name);
    }

    public static int getCommandUsageByPlayerNameOrUuid(UUID uuid){
        return getCommandUsageByPlayerNameOrUuid(uuid.toString());
    }

    private static int getCount(@Nonnull String nameOrUuid){
        String queryString = "SELECT COUNT(*) FROM COMMANDS WHERE (NAME='" + nameOrUuid + "' OR UUID='" + nameOrUuid + "');";
        if (nameOrUuid.equalsIgnoreCase("console") || nameOrUuid.equalsIgnoreCase("server"))
            queryString = "SELECT COUNT(*) FROM COMMANDS WHERE (IP='localhost');";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to add log due to failed db connection");
            return -1;
        }
        int commandUsageCount = 0;

        try {
            commandUsageCount = DBUtils.getCount(db, queryString);
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to get commands usage count for " + nameOrUuid + " from database");
        }

        return commandUsageCount;
    }

    public static int getTotalCount(){
        String queryString = "SELECT COUNT(*) FROM COMMANDS;";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to add log due to failed db connection");
            return -1;
        }
        int commandUsageCount = 0;

        try {
            commandUsageCount = DBUtils.getCount(db, queryString);
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to get commands usage count from database");
        }

        return commandUsageCount;
    }

    public static void deleteLogs(ICommandSender iCommandSender, String target){
        String sqlQuery = "DELETE FROM COMMANDS WHERE NAME='" + target + "' OR UUID='" + target + "';";
        if (target.equalsIgnoreCase("console") || target.equalsIgnoreCase("server"))
            sqlQuery = "DELETE FROM COMMANDS WHERE IP='localhost';";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to delete logs due to failed db connection");
            return;
        }
        try {
            DBUtils.deleteRecord(db, sqlQuery);
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GREEN + target + " command usage logs deleted!");
        } catch (Exception e) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "An Error Occured trying to delete logs! (" + e.toString() + ")");
            LogHelper.error("Error occurred deleting logs (" + e.toString() + ")");
            e.printStackTrace();
        }
    }

    private static String sendCommands(int no, String name, String uuid, String command, String datetime, String ip){
        //1. name (uuid) executed command at datetime with IP
        return TextFormatting.GOLD + "" + no + ". " +
                TextFormatting.AQUA + name + TextFormatting.RESET + " (" + uuid + ") executed " +
                TextFormatting.GREEN + "/" + command + TextFormatting.RESET + " at " +
                TextFormatting.RESET + "" + TextFormatting.ITALIC + datetime + " UTC" +
                TextFormatting.RESET + " with " + TextFormatting.LIGHT_PURPLE + ip;
    }

    private static ArrayList<String> getFullEntityPlayerLogs(String target){
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to check commands stats due to failed db connection");
            return null;
        }
        Statement statement;

        String querySQL = "SELECT NAME,UUID,FULL_COMMAND,TIME,IP FROM COMMANDS WHERE (NAME='" + target + "' OR UUID='" + target + "') ORDER BY TIME DESC;";

        // Check if CONSOLE
        if (target.equalsIgnoreCase("console") || target.equalsIgnoreCase("server"))
            querySQL = "SELECT NAME,UUID,FULL_COMMAND,TIME,IP FROM COMMANDS WHERE (IP='localhost') ORDER BY TIME DESC;";

        try {
            db.setAutoCommit(false);
            statement = db.createStatement();
            ResultSet rs = statement.executeQuery(querySQL);
            int i = 1;
            ArrayList<String> commandHist = new ArrayList<>();
            while (rs.next()){
                commandHist.add(sendCommands(i, rs.getString("NAME"), rs.getString("UUID"), rs.getString("FULL_COMMAND"), rs.getString("TIME"), rs.getString("IP")));
                i++;
            }
            rs.close();
            statement.close();
            db.close();
            return commandHist;
        } catch (Exception e) {
            LogHelper.error("Error Occurred parsing player logs (" + e.toString() + ")");
            e.printStackTrace();
            return null;
        }
    }

    public static void checkCommandLogs(ICommandSender p, String target, int no){
        ArrayList<String> commandList = getFullEntityPlayerLogs(target);
        if (commandList == null){
            //Exception
            ChatHelper.sendMessage(p, TextFormatting.RED + "An Error Occured trying to get logs!");
        } else {
            parseMessages(commandList, p, no, target);
        }
    }

    private static void parseMessages(ArrayList<String> stringList, ICommandSender iCommandSender, int arg, String target){
        int maxPossibleValue = stringList.size();	//Max possible based on stringList
        int maxPossiblePage = (stringList.size() / 10) + 1;
        if (maxPossiblePage < arg){
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Max amount of pages is " + maxPossiblePage + ". Please specify a value within that range!");
            return;
        }
        //1 (0-9), 2 (10,19)...
        int minValue = (arg - 1) * 10;
        int maxValue = (arg * 10) - 1;
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "--- Command History For " + target + " Page " + arg + " of " + maxPossiblePage + " ---");
        if (maxValue > maxPossibleValue) {	//Exceeds
            for (int i = minValue; i < stringList.size(); i++){
                ChatHelper.sendMessage(iCommandSender, stringList.get(i));
            }
        } else {
            for (int i = minValue; i <= maxValue; i++) {
                ChatHelper.sendMessage(iCommandSender, stringList.get(i));
            }
        }
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "-------------------------------------------");
    }

    public static void checkCommandStats(ICommandSender p, String target, UUID uuid){
        int commands = getCount(target);
        if (commands == -2){
            ChatHelper.sendMessage(p, TextFormatting.RED + "An Error Occured trying to convert command usage count!");
            return;
        } else if (commands == -1){
            ChatHelper.sendMessage(p, TextFormatting.RED + "An Error Occured trying to get command usage stats!");
            return;
        }

        //Present them all out
        ChatHelper.sendMessage(p, TextFormatting.GOLD + "--- Command Usage Statistics ---");
        ChatHelper.sendMessage(p, TextFormatting.GOLD + "Name: " + TextFormatting.RESET + target);
        ChatHelper.sendMessage(p, TextFormatting.GOLD + "UUID: " + TextFormatting.RESET + uuid);
        ChatHelper.sendMessage(p, TextFormatting.GOLD + "Command Usage Counts: " + TextFormatting.RESET + commands);
        ChatHelper.sendMessage(p, TextFormatting.GOLD + "-----------------------------");
    }
}
