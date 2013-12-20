import co.mccn.tokenapi.TokenAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Example extends JavaPlugin implements Listener, CommandExecutor {

    private TokenAPI tokenAPI;

    @Override
    public void onEnable() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("TokenAPI");
        if (plugin == null) {
            this.getLogger().severe("TokenAPI not found! Disabling...");
            this.getPluginLoader().disablePlugin(this);
            return;
        } else {
            this.tokenAPI = (TokenAPI) plugin;
        }

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("token").setExecutor(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        tokenAPI.getTokens(event.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            this.getLogger().info("This is a player-only command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("" + this.tokenAPI.getTokens(sender.getName()));
        } else if (args.length == 1) {
            int tokens = this.tokenAPI.getTokens(args[1]);

            if (tokens == -1) {
                sender.sendMessage("That player has never played here before.");
            } else {
                sender.sendMessage("" + tokens);
            }
        } else if (args.length == 3) {
            if (this.getServer().getOfflinePlayer(args[1]) == null) {
                sender.sendMessage("That player has never played here before.");
            } else {
                switch (args[0].toLowerCase()) {
                    case "set":
                        this.tokenAPI.setTokens(args[1], Integer.parseInt(args[2]));
                        break;
                    case "update":
                        this.tokenAPI.updateTokens(args[1], Integer.parseInt(args[2]));
                        break;
                    default:
                        sender.sendMessage("Incorrect argument!");
                }
            }
        } else {
            sender.sendMessage("Incorrect number of arguments!");
        }
        return true;
    }
}
