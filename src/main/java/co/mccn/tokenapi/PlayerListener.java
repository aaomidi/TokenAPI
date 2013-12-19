package co.mccn.tokenapi;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final TokenAPI _plugin;

    public PlayerListener(TokenAPI plugin) {
        _plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        _plugin.playerBalance.put(e.getPlayer().getName(), _plugin.mysql.getPlayerBalance(e.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (_plugin.playerBalance.containsKey(e.getPlayer().getName())) {
            _plugin.playerBalance.remove(e.getPlayer().getName());
        }
    }
}
