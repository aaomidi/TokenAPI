package co.mccn.tokenapi;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenAPI extends JavaPlugin {
    public final Logger logger = Logger.getLogger("Minecraft");
    protected HashMap<String, Integer> playerBalance = new HashMap<>();
    protected DatabaseHandler mysql;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        mysql = new DatabaseHandler(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        logger.log(Level.INFO, "{0} v{1} has been enabled.", new Object[]{getDescription().getName(), getDescription().getVersion()});
        this.getCommand("tokenapi").setExecutor(new Commands(this));
    }
}