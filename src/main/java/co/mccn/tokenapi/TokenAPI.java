package co.mccn.tokenapi;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TokenAPI extends JavaPlugin {

    private Database database;
    private Map<String, Integer> tokenMap = new LinkedHashMap<>();

    @Override
    public final void onDisable() {
        if (this.database != null) {
            this.updateDatabase();
            this.database.disconnect();
        }
    }

    @Override
    public final void onLoad() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
            this.getLogger().severe("THIS IS NOT A PLUGIN. THIS IS AN API FOR OTHER PLUGINS TO USE. DO NOT EXPECT THIS PLUGIN TO DO ANYTHING.");
        }
    }

    @Override
    public final void onEnable() {
        try {
            this.database = new Database(this);
        } catch (SQLException ex) {
            this.getLogger().severe("Error connecting to database! Disabling...");
            ex.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        this.updateTokenMap();
    }

    public final Map<String, Integer> getTokenMap() {
        return this.tokenMap;
    }

    public final int getTokens(Player player) {
        return this.getTokens(player.getName());
    }

    public final int getTokens(String playerName) {
        if (!this.tokenMap.containsKey(playerName)) {
            if (this.getServer().getOfflinePlayer(playerName) != null) {
                this.tokenMap.put(playerName, this.getConfig().getInt("start-value"));
            } else {
                return -1;
            }
        }

        return this.tokenMap.get(playerName);
    }

    public final void setTokens(Player player, int amount) {
        this.setTokens(player.getName(), amount);
    }

    public final void setTokens(String playerName, int amount) {
        this.tokenMap.put(playerName, amount);
    }

    public final void updateTokens(Player player, int diff) {
        this.updateTokens(player.getName(), diff);
    }

    public final void updateTokens(String playerName, int diff) {
        this.setTokens(playerName, this.getTokens(playerName) + diff);
    }

    public final void updateTokenMap() {
        String query = "SELECT `player`, `tokens` FROM `tokens`";
        ResultSet resultSet = this.database.executeQuery(query);

        try {
            if (resultSet == null || resultSet.isClosed()) {
                return;
            }

            while (resultSet.next()) {
                this.tokenMap.put(resultSet.getString("player"), resultSet.getInt("tokens"));
            }
        } catch (SQLException ex) {
            /*
             * Before anyone (Zenexer) loses their shit, this is debug and will be removed once I can confirm
             * I am doing MySQL correctly.
             */
            ex.printStackTrace();
        }
    }

    public final void updateTokenMap(String playerName) {
        String query = "SELECT `tokens` FROM `tokens` WHERE `player`=?";
        ResultSet resultSet = this.database.executeQuery(query, playerName);

        try {
            if (resultSet != null) {
                this.tokenMap.put(playerName, resultSet.getInt("tokens"));
            }
        } catch (SQLException ex) {
            /*
             * Before anyone (Zenexer) loses their shit, this is debug and will be removed once I can confirm
             * I am doing MySQL correctly.
             */
            ex.printStackTrace();
        }
    }

    public final void updateDatabase() {
        this.database.executeUpdate("TRUNCATE `tokens`;");

        String query = "INSERT INTO `tokens` VALUES(?,?);";
        for (Map.Entry<String, Integer> entry : this.tokenMap.entrySet()) {
            this.database.executeUpdate(query, entry.getKey(), entry.getValue());
        }
    }

    public final void updateDatabase(String playerName) {
        String query = "UPDATE `tokens` SET `tokens`=? WHERE`player`=?;";
        String query2 = "INSERT INTO `tokens` VALUES(?,?);";

        int tokens = this.getTokens(playerName);

        int rows = this.database.executeUpdate(query, tokens, playerName);

        if (rows == 0) {
            this.database.executeUpdate(query2, playerName, tokens);
        }
    }
}