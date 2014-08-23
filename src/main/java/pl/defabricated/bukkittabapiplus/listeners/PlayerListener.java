package pl.defabricated.bukkittabapiplus.listeners;

import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.defabricated.bukkittabapiplus.TabPlugin;
import pl.defabricated.bukkittabapiplus.api.TabAPI;
import pl.defabricated.bukkittabapiplus.api.TabList;

import java.util.Random;

public class PlayerListener implements Listener{

    TabPlugin plugin;

    public PlayerListener(TabPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        plugin.initPlayer(event.getPlayer());
        for (Player online : Bukkit.getOnlinePlayers()) {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(online.getPlayerListName(), false, -1);
            ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        plugin.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event){
        plugin.removePlayer(event.getPlayer());
    }

}
