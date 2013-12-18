package co.mccn.tokenapi;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created with IntelliJ IDEA.
 * User: Amir
 * Date: 12/18/13
 * Time: 3:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class Commands implements CommandExecutor {
    private TokenAPI _plugin;
    private int tokens;

    public Commands(TokenAPI plugin) {
        _plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("TokenAPI")) {
            if (args.length == 0) {
                tokens = _plugin.playerBalance.get(sender.getName());
                sendMessage(sender, "&bYou currently have &a" + tokens + "&b Tokens");
                return true;
            } else if (args.length == 1) {
                if (_plugin.getServer().getOfflinePlayer(args[0]) == null) {
                    sendMessage(sender, "&cPlayer was not found");

                } else {

                    if (!(_plugin.playerBalance.containsKey(_plugin.getServer().getOfflinePlayer(args[0]).getName()))) {

                        tokens = _plugin.mysql.getPlayerBalance(_plugin.getServer().getOfflinePlayer(args[0]).getName());
                        sendMessage(sender, "&b" + _plugin.getServer().getOfflinePlayer(args[0]).getName() + " has &a" + tokens + " &btokens");
                        return true;
                    } else {
                        tokens = _plugin.playerBalance.get(_plugin.getServer().getOfflinePlayer(args[0]).getName());
                        sendMessage(sender, "&b" + _plugin.getServer().getOfflinePlayer(args[0]).getName() + " has &a" + tokens + " &btokens");
                        return true;
                    }
                }
            } else if (args.length == 2) {
                sendMessage(sender, "&cThe correct command syntax is /tokenapi [set|add] player Number");
                return false;
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (!(_plugin.getServer().getOfflinePlayer(args[1]) == null)) {
                        try {

                            tokens = Integer.parseInt(args[2]);
                           _plugin.mysql.setPlayer(_plugin.getServer().getOfflinePlayer(args[1]).getName(), tokens);
                            sendMessage(sender, "&b"+_plugin.getServer().getOfflinePlayer(args[1]).getName()+" now has &a"+tokens+" &btokens.");
                            return true;
                        } catch (NumberFormatException ex) {
                            sendMessage(sender, "&cPlease enter an integer");
                            ex.printStackTrace();
                        }
                    } else {
                        sendMessage(sender, "&cPlayer was not found");
                    }
                } else if (args[0].equalsIgnoreCase("add")) {
                    if (!(_plugin.getServer().getOfflinePlayer(args[1]) == null)) {
                        String tokenCounts[] = args[2].split("(\\D)");

                        try {
                            int tokens = Integer.parseInt(tokenCounts[1]);

                            if (args[2].charAt(0) == '+') {
                                _plugin.mysql.updatePlayer(_plugin.getServer().getOfflinePlayer(args[1]).getName(), tokens, '+');
                                sendMessage(sender, "&b"+_plugin.getServer().getOfflinePlayer(args[1]).getName()+" now has &a"+_plugin.playerBalance.get(_plugin.getServer().getOfflinePlayer(args[1]).getName())+" &btokens.");
                                return true;
                            } else if (args[2].charAt(0) == '-') {
                                _plugin.mysql.updatePlayer(_plugin.getServer().getOfflinePlayer(args[1]).getName(), tokens, '-');
                                sendMessage(sender, "&b"+_plugin.getServer().getOfflinePlayer(args[1]).getName()+" now has &a"+_plugin.playerBalance.get(_plugin.getServer().getOfflinePlayer(args[1]).getName())+" &btokens.");
                                return true;
                            } else {
                                sendMessage(sender, "&cPlease specify if this is an addition or subtraction. Example: /tokenAPI add aaomidi +10");
                            }
                        } catch (NumberFormatException ex) {
                            sendMessage(sender, "&cPlease enter an integer");
                            ex.printStackTrace();
                        }
                    } else {
                        sendMessage(sender, "&cPlayer was not found");
                    }

                }
            }
        }
        return false;
    }

    public void sendMessage(CommandSender sender, String message) {
        String prefix = "&7[&6TokenAPI&7] ";
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

}
