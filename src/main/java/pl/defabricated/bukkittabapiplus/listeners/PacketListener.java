package pl.defabricated.bukkittabapiplus.listeners;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import pl.defabricated.bukkittabapiplus.TabPlugin;
import pl.defabricated.bukkittabapiplus.api.TabList;
import pl.defabricated.bukkittabapiplus.api.TabSlot;

public class PacketListener extends PacketAdapter {

    TabPlugin plugin;

    public PacketListener(TabPlugin plugin) {
        super(plugin, ConnectionSide.SERVER_SIDE, Packets.Server.PLAYER_INFO);
        this.plugin = plugin;
        plugin.protocolManager.addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent event){
        if (!event.isCancelled() && event.getPacketID() == Packets.Server.PLAYER_INFO) {
            PacketContainer packet = event.getPacket();
            Player player = event.getPlayer();
            int ping = packet.getIntegers().read(0);
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
                packet.getIntegers().write(0, ping);
                event.setPacket(packet);
                return;
            } else {
                event.setCancelled(true);
            }
        }
    }

}
