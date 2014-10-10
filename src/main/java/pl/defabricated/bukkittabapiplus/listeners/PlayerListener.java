package pl.defabricated.bukkittabapiplus.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
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

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class PlayerListener implements Listener{

    TabPlugin plugin;

    public PlayerListener(TabPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        for (Player online : Bukkit.getOnlinePlayers()) {
            PacketContainer packet = plugin.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            packet.getStrings().write(0, online.getPlayerListName());
            packet.getBooleans().write(0, false);
            packet.getIntegers().write(0, -1);
            try {
                plugin.protocolManager.sendServerPacket(event.getPlayer(), packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
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
