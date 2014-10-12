package pl.defabricated.bukkittabapiplus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.defabricated.bukkittabapiplus.api.TabAPI;
import pl.defabricated.bukkittabapiplus.api.TabList;
import pl.defabricated.bukkittabapiplus.listeners.PacketListener;
import pl.defabricated.bukkittabapiplus.listeners.PlayerListener;

import java.lang.reflect.Field;
import java.util.*;

public class TabPlugin extends JavaPlugin {

    TabAPI api;

    PlayerListener playerListener;
    PacketListener packetListener;

    public ProtocolManager protocolManager;

    @Override
    public void onEnable(){
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        this.api = new TabAPI(this);

        this.playerListener = new PlayerListener(this);
        this.packetListener = new PacketListener(this);
    }

    @Override
    public void onDisable(){
        for(Player online : Bukkit.getOnlinePlayers()){
            TabList list = tabLists.get(online.getName());
            if(list != null) {
                list.clear();
            }
        }
    }

    public HashMap<String, TabList> tabLists = new HashMap();

    public void removePlayer(Player player){
        tabLists.remove(player.getName());
    }

    public PacketContainer buildTeamPacket(String name, String display, String prefix, String suffix, int flag, String... members) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, flag);
        packet.getStrings().write(0, name).write(1, display).write(2, prefix).write(3, suffix);
        packet.getSpecificModifier(Collection.class).write(0, Arrays.asList(members));
        return packet;
    }

}
