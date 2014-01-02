package co.mccn.tokenapi;

import co.mccn.mccnsql.MCCNSQL;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class TokenAPI extends JavaPlugin {

    private Map<String, Integer> tokenMap = new LinkedHashMap<>();
    public MCCNSQL mccnSql;

    @Override
    public final void onDisable() {
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
        getServer().getPluginManager().registerEvents(new Events(this), this);
        Plugin plugin = getServer().getPluginManager().getPlugin("MCCNSQL");
        if (plugin == null) {
            getLogger().log(Level.SEVERE, "MCCNSQL not found, Disabling...");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            mccnSql = (MCCNSQL) plugin;
        }
        mccnSql.setDatabase(getConfig().getString("database"));
        mccnSql.connect();
        createDatabase();

    }

    private void createDatabase() {
        mccnSql.executeUpdate("CREATE TABLE IF NOT EXISTS `tokens` (`player` varchar(16) NOT NULL, `tokens` int(11) NOT NULL DEFAULT '0', PRIMARY KEY(`player`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
    }

    public final Map<String, Integer> getTokenMap() {
        return this.tokenMap;
    }

    public final int getTokens(Player player) {
        return this.getTokens(player.getName());
    }

    public final int getTokens(String playerName) {
        if (!this.tokenMap.containsKey(playerName)) {
            if (this.getServer().getOfflinePlayer(playerName).hasPlayedBefore()) {
                String query = "SELECT `tokens` FROM `tokens` WHERE `player`=?";
                ResultSet resultSet = mccnSql.executeQuery(query, playerName);
                try {
                    if (resultSet.next()) {
                        do {
                            int tokens = resultSet.getInt("tokens");
                            this.tokenMap.put(playerName, tokens);
                            return this.tokenMap.get(playerName);
                        } while (resultSet.next());
                    }
                    return -1;
                } catch (SQLException ex) {
                    getLogger().log(Level.SEVERE, ex.getMessage());
                }
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
        if (tokenMap.containsKey(playerName)) {
            this.tokenMap.put(playerName, amount);
            this.updateDatabase(playerName);
        } else {
            if (getServer().getOfflinePlayer(playerName) != null) {
                String query = "UPDATE `tokens` SET `tokens`=? WHERE `player`=?";
                mccnSql.executeUpdate(query, amount, playerName);

            }

        }

    }

    public final void updateTokens(Player player, int diff) {
        this.updateTokens(player.getName(), diff);
    }

    public final void updateTokens(String playerName, int diff) {

        this.setTokens(playerName, this.getTokens(playerName) + diff);

    }

    /*  public final void updateTokenMap() {
          String query = "SELECT `player`, `tokens` FROM `tokens`";
         ResultSet resultSet = this.database.executeQuery(query);

          try {
              if (resultSet == null || resultSet.isClosed()) {
                  return;
              }

              while (resultSet.next()) {
                  if (resultSet.getString())
                  this.tokenMap.put(resultSet.getString("player"), resultSet.getInt("tokens"));
              }
          } catch (SQLException ex) {

      ex.printStackTrace();
  }
  }

  public final void updateTokenMap(String playerName){
          String query="SELECT `tokens` FROM `tokens` WHERE `player`=?";
  ResultSet resultSet=this.database.executeQuery(query,playerName);

  try{
          if(resultSet!=null){
          this.tokenMap.put(playerName,resultSet.getInt("tokens"));
  }
          }catch(SQLException ex){

          ex.printStackTrace();
  }
          }
   */
    public final void updateDatabase() {

        String query = "UPDATE `tokens` set `tokens`=? WHERE `player`=?;";
        for (Map.Entry<String, Integer> entry : this.tokenMap.entrySet()) {
            mccnSql.executeUpdate(query, entry.getValue(), entry.getKey());
        }
    }

    public final void updateDatabase(String playerName) {
        String query = "UPDATE `tokens` SET `tokens`=? WHERE `player`=?;";

        int tokens = this.getTokens(playerName);

        mccnSql.executeUpdate(query, tokens, playerName);

    }

    public final void initializePlayer(Player player) {
        this.initializePlayer(player.getName());
    }

    public final void initializePlayer(String playerName) {
            String query = "INSERT IGNORE INTO `tokens` (`player`,`tokens`) VALUES(?, ?);";
        String query2 = "SELECT `tokens` FROM `tokens` WHERE `player`=?;";
        mccnSql.executeUpdate(query, playerName, 0);
        ResultSet resultSet = mccnSql.executeQuery(query2, playerName);
        try {
            if (resultSet.next()) {
                do {
                    tokenMap.put(playerName, resultSet.getInt("tokens"));
                } while (resultSet.next());
            } else {
                getLogger().log(Level.SEVERE, "Error has occurred in the plugin, please check your database connection.");

            }
        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE, ex.getMessage());

        }

    }
}