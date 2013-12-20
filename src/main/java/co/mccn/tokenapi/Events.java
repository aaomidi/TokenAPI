package co.mccn.tokenapi;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created with IntelliJ IDEA.
 * User: aaomidi
 * Date: 12/19/13
 * Time: 9:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Events implements Listener {
    private final TokenAPI _plugin;

    public Events(TokenAPI plugin) {
        _plugin = plugin;
    }

    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        _plugin.initializePlayer(player);
    }

    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        _plugin.updateDatabase(player.getName());
        _plugin.getTokenMap().remove(player.getName());

    }
}
