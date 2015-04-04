package pl.defabricated.bukkittabapiplus.listeners;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.defabricated.bukkittabapiplus.TabPlugin;
import pl.defabricated.bukkittabapiplus.api.TabList;
import pl.defabricated.bukkittabapiplus.api.TabSlot;

public class PacketListener extends PacketAdapter {

    TabPlugin plugin;

    public PacketListener(TabPlugin plugin) {
        super(plugin, ConnectionSide.SERVER_SIDE, Packets.Server.PLAYER_INFO, Packets.Server.LOGIN);
        this.plugin = plugin;
        plugin.protocolManager.addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent event){
        if(event.isCancelled()) {
            return;
        }
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();

        if(plugin.protocolManager.getProtocolVersion(player) >= 47) {
            return;
        }

        if (event.getPacketID() == Packets.Server.PLAYER_INFO) {
            int ping = packet.getIntegers().read(0);
            if(ping != -1) {
                try {
                    ping = packet.getIntegers().read(2);
                } catch (Exception ex) { }
            }
            if (ping == -1) {
                TabList list = plugin.tabLists.get(player.getName());
                ping = list.getDefaultPing();
                String name = packet.getStrings().read(0);

                for(int i=0; i<60; i++){
                    TabSlot slot = list.getSlot(i);
                    if(slot != null && slot.getName().equals(name)){
                        ping = slot.getPing();
                        break;
                    }
                }

                for(int i=0; i<60; i++){
                    TabSlot slot = list.getSlot(i);
                    for(int j=0; j<60; j++) {
                        TabSlot tabSlot = list.getSlot(j);
                        if(slot != null && tabSlot != null && i != j && slot.getName().equals(tabSlot.getName())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                packet.getIntegers().write(0, ping);
                try {
                    packet.getIntegers().write(2, ping);
                } catch (Exception ex) { }
                event.setPacket(packet);
                return;
            } else {
                event.setCancelled(true);
            }
        }

        if(event.getPacketID() == Packets.Server.LOGIN) {
            packet.getIntegers().write(2, 60); //Force maximum TabList size
            event.setPacket(packet);
        }
    }

}
