package com.itachi1706.cheesecakeservercommands.dbstorage;

import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 9/12/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.dbstorage
 */
public class LoginLogoutDB extends BaseSQLiteDB {

    private static LoginLogoutDB instance;

    private static final String TYPE_LOGIN = "LOGIN";
    private static final String TYPE_LOGOUT = "LOGOUT";

    public LoginLogoutDB() {
        super("logins");
    }

    public static LoginLogoutDB getInstance() {
        if (instance == null) {
            instance = new LoginLogoutDB();
        }
        return instance;
    }

    @Override
    public void checkTablesExists(){
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

        if (!createTable(db, createDB, "Login/Logout"))
            LogHelper.error("Login/Logout DB fail to ensure that table is created");
    }

    private void addLog(ServerPlayer player, String type){
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
            if (ServerPlayerUtil.isOperator(player) && type.equalsIgnoreCase(TYPE_LOGIN)) {
                try { // Check if ODBC driver is installed if not notify them
                    Class.forName("org.sqlite.JDBC"); // Success
                } catch (ClassNotFoundException e) {
                    TextUtil.sendChatMessage(player, ChatFormatting.RED + "SQLite ODBC driver not installed. Please inform the server administrator to put it into the mods folder!"); // Fail.
                }
            }
            return;
        }

        insertRecord(db, insertQuery, "Unable to insert login record");
    }

    public void addLoginLog(Player player){
        addLog((ServerPlayer) player, TYPE_LOGIN);
        LogHelper.info("Logged Login for " + player.getStringUUID());
    }

    public void addLogoutLog(Player player){
        addLog((ServerPlayer) player, TYPE_LOGOUT);
        LogHelper.info("Logged Logout for " + player.getStringUUID());
    }

    public int getLoginCount(String name){
        return getCount(name, TYPE_LOGIN);
    }

    public int getLogoutCount(String name){
        return getCount(name, TYPE_LOGOUT);
    }

    private int getCount(String name, String type){
        String queryString = "SELECT COUNT(*) FROM LOGINS WHERE (NAME='" + name + "' OR UUID='" + name + "') AND TYPE='" + type + "';";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to add log due to failed db connection");
            return -1;
        }
        int loginCount = 0;

        try {
            loginCount = getCount(db, queryString);
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.error("Unable to get " + type + " count from database");
        }

        return loginCount;
    }

    public void deleteLogs(CommandSourceStack sender, String target){
        String sqlQuery = "DELETE FROM LOGINS WHERE NAME='" + target + "' OR UUID='" + target + "';";
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to delete logs due to failed db connection");
            return;
        }
        try {
            deleteRecord(db, sqlQuery);
            TextUtil.sendChatMessage(sender, ChatFormatting.GREEN + target + " logs for login/logout deleted!");
        } catch (Exception e) {
            TextUtil.sendChatMessage(sender, ChatFormatting.RED + "An Error Occured trying to delete logs! (" + e + ")");
            LogHelper.error("Error occurred deleting logs (" + e + ")");
            e.printStackTrace();
        }
    }

    private String sendLogin(int no, String x, String y, String z, String world, String datetime, String ip){
        //1. datetime Login at X: x, Y: y, Z:z, World: world with IP
        return (ChatFormatting.GOLD + "") + no + ". " +
                ChatFormatting.RESET + "" + ChatFormatting.ITALIC + datetime + " UTC " + ChatFormatting.RESET + "" +
                ChatFormatting.GREEN + "Login " + ChatFormatting.RESET + "at " + ChatFormatting.GOLD + "" +
                ChatFormatting.AQUA + world + ChatFormatting.GOLD + "," + ChatFormatting.AQUA + x +
                ChatFormatting.GOLD + "," + ChatFormatting.AQUA + y + ChatFormatting.GOLD + "," +
                ChatFormatting.AQUA + z + ChatFormatting.RESET + " at " + ChatFormatting.LIGHT_PURPLE + ip;
    }

    private String sendLogout(int no, String x, String y, String z, String world, String datetime, String ip){
        //1. datetime Logout at X: x, Y: y, Z:z, World: world with IP
        return (ChatFormatting.GOLD + "") + no + ". " +
                ChatFormatting.RESET + "" + ChatFormatting.ITALIC + datetime + " UTC " + ChatFormatting.RESET + "" +
                ChatFormatting.RED + "Logout " + ChatFormatting.RESET + "at " + ChatFormatting.GOLD + "" +
                ChatFormatting.AQUA + world + ChatFormatting.GOLD + "," + ChatFormatting.AQUA + x +
                ChatFormatting.GOLD + "," + ChatFormatting.AQUA + y + ChatFormatting.GOLD + "," +
                ChatFormatting.AQUA + z + ChatFormatting.RESET + " at " + ChatFormatting.LIGHT_PURPLE + ip;
    }

    @Override
    protected void parseMessages(List<String> stringList, CommandSourceStack sender, int arg, String target){
        parseMessages(stringList, sender, arg, target, "Login History");
    }

    public void checkLoginStats(CommandSourceStack p, String target, UUID uuid, String firstPlayed, String lastPlayed){
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
                gamemode = switch (pl.gameMode.getGameModeForPlayer()) {
                    case CREATIVE -> ChatFormatting.GREEN + "CREATIVE";
                    case SURVIVAL -> ChatFormatting.GREEN + "SURVIVAL";
                    case ADVENTURE -> ChatFormatting.GREEN + "ADVENTURE";
                    case SPECTATOR -> ChatFormatting.GREEN + "SPECTATOR";
                };

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
                gamemode = switch (s) {
                    case "creative" -> ChatFormatting.RED + "CREATIVE";
                    case "survival" -> ChatFormatting.RED + "SURVIVAL";
                    case "adventure" -> ChatFormatting.RED + "ADVENTURE";
                    default -> ChatFormatting.GRAY + "UNSET (REQUIRES PLAYER LOGIN TO SET)";
                };
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

    @Nonnull
    private ArrayList<String> getFullEntityPlayerLogs(String target){
        Connection db = getSQLiteDBConnection();
        if (db == null){
            LogHelper.error("Unable to check login stats due to failed db connection");
            return new ArrayList<>();
        }

        String querySQL = "SELECT NAME,UUID,TYPE,X,Y,Z,WORLD,TIME,IP FROM LOGINS WHERE (NAME='" + target + "' OR UUID='" + target + "') ORDER BY TIME DESC;";

        try (Statement statement = db.createStatement()) {
            db.setAutoCommit(false);
            ResultSet rs = statement.executeQuery(querySQL);
            int i = 1;
            ArrayList<String> loginHist = new ArrayList<>();
            while (rs.next()){
                if (rs.getString("TYPE").equalsIgnoreCase(TYPE_LOGIN)){
                    //A login message
                    loginHist.add(sendLogin(i, rs.getString("X"), rs.getString("Y"), rs.getString("Z"), rs.getString("WORLD"), rs.getString("TIME"), rs.getString("IP")));
                } else if (rs.getString("TYPE").equalsIgnoreCase(TYPE_LOGOUT)){
                    //A logout message
                    loginHist.add(sendLogout(i, rs.getString("X"), rs.getString("Y"), rs.getString("Z"), rs.getString("WORLD"), rs.getString("TIME"), rs.getString("IP")));
                }
                i++;
            }
            rs.close();
            db.close();
            return loginHist;
        } catch (Exception e) {
            LogHelper.error("Error Occurred parsing player logs (" + e + ")");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void checkLoginLogs(CommandSourceStack p, String target, int no){
        ArrayList<String> loginHist = getFullEntityPlayerLogs(target);
        if (loginHist.isEmpty()){
            //Exception
            TextUtil.sendChatMessage(p, ChatFormatting.RED + "An Error Occurred trying to get logs!");
        } else {
            parseMessages(loginHist, p, no, target);
        }
    }
}
