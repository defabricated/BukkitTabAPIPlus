package pl.defabricated.bukkittabapiplus.api;

import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pl.defabricated.bukkittabapiplus.TabPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class TabList {

    TabPlugin plugin;

    Player player;

    int defaultPing = 1000;

    TabList(TabPlugin plugin, Player player){
        this.plugin = plugin;
        this.player = player;
    }

    HashMap<Integer, TabSlot> slots = new HashMap();
    HashMap<Integer, TabSlot> toRemove = new HashMap();

    public TabSlot getSlot(int slot){
        return slots.get(slot);
    }

    public void setDefaultPing(int ping){
        defaultPing = ping;
    }

    public int getDefaultPing(){
        return defaultPing;
    }

    public void clearSlot(int slot){
        TabSlot tabSlot = slots.remove(slot);
        if(tabSlot == null){
            return;
        }
        tabSlot.toRemove = true;
        toRemove.put(slot, tabSlot);
    }

    public TabSlot setSlot(int slot, String name){
        TabSlot tabSlot = new TabSlot(this, name);
        slots.put(slot, tabSlot);
        return tabSlot;
    }

    public TabSlot setSlot(int slot, String prefix, String name, String suffix){
        TabSlot tabSlot = new TabSlot(this, prefix, name, suffix);
        slots.put(slot, tabSlot);
        return tabSlot;
    }

    public void send(){
        for(int i=0; i<60; i++){
            TabSlot slot = slots.get(i);
            if(slot != null){
                slot.sent = true;
                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(slot.getName(), true, -1);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
                if(slot.teamExists){
                    PacketPlayOutScoreboardTeam team = plugin.buildTeamPacket(slot.getName(), slot.getName(), slot.getPrefix(), slot.getSuffix(), 0, slot.getName());
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(team);
                }
            } else {
                String nullName = "§" + String.valueOf(i);
                if (i >= 10) {
                    nullName = "§" + String.valueOf(i / 10) + "§" + String.valueOf(i % 10);
                }
                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(nullName, true, -1);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
            }

        }
    }

    public void update(){
        clear();
        send();
    }

    public void clear(){
        for(int i=0; i<60; i++){
            TabSlot slot = toRemove.get(i);
            if(slot == null){
                slot = slots.get(i);
            }
            if(slot != null){
                slot.sent = false;
                if(slot.teamExists){
                    PacketPlayOutScoreboardTeam team = plugin.buildTeamPacket(slot.getName(), slot.getName(), null, null, 1, slot.getName());
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(team);
                }
                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(slot.getName(), false, -1);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
            } else {
                String nullName = "§" + String.valueOf(slot);
                if (i >= 10) {
                    nullName = "§" + String.valueOf(i / 10) + "§" + String.valueOf(i % 10);
                }
                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(nullName, false, -1);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
            }
        }
        toRemove.clear();
    }

}
