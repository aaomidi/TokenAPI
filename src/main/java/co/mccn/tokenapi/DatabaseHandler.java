package co.mccn.tokenapi;

import java.sql.*;
import java.util.logging.Level;

public class DatabaseHandler {

    private final TokenAPI _plugin;
    protected static String connector = "jdbc:mysql://";
    protected static String username, password;

    public DatabaseHandler(TokenAPI plugin) {
        _plugin = plugin;

        String host = _plugin.getConfig().getString("mysql.host");
        int port = _plugin.getConfig().getInt("mysql.port");
        String database = _plugin.getConfig().getString("mysql.database");

        connector += host + ":" + port + "/" + database;

        username = _plugin.getConfig().getString("mysql.username");
        password = _plugin.getConfig().getString("mysql.password");

        createTables();
    }

    private Connection openConnection() {
        try {
            Connection con = DriverManager.getConnection(connector, username, password);
            return con;
        } catch (SQLException e) {
            return null;
        }
    }

    private Boolean closeConnection(Connection con) {
        try {
            con.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private void createTables() {
        Connection connection = openConnection();

        try {
            Statement stmt = connection.createStatement();
            _plugin.logger.log(Level.INFO, "Setting up the 'tokens' table");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `tokens` (`player` varchar(16) NOT NULL, `tokens` int(11) NOT NULL DEFAULT '0', PRIMARY KEY(`player`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        closeConnection(connection);
    }

    public int getPlayerBalance(String username) {
        String query = "SELECT `tokens` FROM `tokens` WHERE player = '" + username + "';";

        Connection connection = openConnection();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getInt("tokens");
            } else {
                query = "INSERT INTO `tokens` (player, tokens) VALUES('" + username + "', 0)";
                stmt.executeUpdate(query);
            }
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public void updatePlayer(String username, int update, char method) {
        if (method == '+') {
            _plugin.playerBalance.put(username, _plugin.playerBalance.get(username) + update);
        } else {
            _plugin.playerBalance.put(username, _plugin.playerBalance.get(username) - update);
            if (_plugin.playerBalance.get(username) <= 0) {
                method = ' ';
                update = 0;
            }
        }

        String query = "UPDATE `tokens` SET `tokens` = (`tokens` " + method + update + ") WHERE `player` = '" + username + "'";

        Connection connection = openConnection();

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);

            _plugin.logger.log(Level.INFO, "Player blance update [{0} = {1}]", new Object[]{username, update});

            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        closeConnection(connection);
    }

    public void setPlayer(String username, int value) {

        String query = "UPDATE `tokens` SET `tokens` = '" + value + "' " + "WHERE `player` = '" + username + "'";
        Connection connection = openConnection();
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);
            stmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        _plugin.playerBalance.put(username, value);
        closeConnection(connection);
    }

    public void initializePlayer(String username) {
        String queryCheck = "SELECT `tokens` FROM `tokens` WHERE `player` = '" + username + "'";
        String queryAdd = "INSERT INTO `tokens` (`player`, `tokens`) VALUES (" + username + ", 0)";
        Connection connection = openConnection();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryCheck);
            if (rs == null) {
                stmt.executeUpdate(queryAdd);
                _plugin.playerBalance.put(username, 0);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        closeConnection(connection);
    }
}
