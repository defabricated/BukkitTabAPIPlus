package pl.defabricated.bukkittabapiplus;

import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import pl.defabricated.bukkittabapiplus.api.TabAPI;
import pl.defabricated.bukkittabapiplus.api.TabList;
import pl.defabricated.bukkittabapiplus.api.TabSlot;
import pl.defabricated.bukkittabapiplus.listeners.PlayerListener;

import java.lang.reflect.Field;
import java.util.*;

public class TabPlugin extends JavaPlugin {

    TabAPI api;

    private Field clientChannel;

    PlayerListener playerListener;

    public HashMap<String, Channel> channels = new HashMap();
    public List<Channel> channelList = new ArrayList();

    @Override
    public void onEnable(){
        this.api = new TabAPI(this);

        this.playerListener = new PlayerListener(this);

        try {
            clientChannel = NetworkManager.class.getDeclaredField("m");
            clientChannel.setAccessible(true);
        } catch (Exception e) {
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            initPlayer(player);
        }
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
        for(Player player : Bukkit.getOnlinePlayers()){
            unregisterPlayer(player);
        }
    }

    public HashMap<String, TabList> tabLists = new HashMap();

    public void removePlayer(Player player){
        tabLists.remove(player.getName());
    }

    public void initPlayer(Player player){
        try {
            final EntityPlayer p = ((CraftPlayer) player).getHandle();

            Channel channel = (Channel) clientChannel.get(p.playerConnection.networkManager);

            channels.put(p.getName(), channel);

            channel.pipeline().addBefore("packet_handler", "TabAPI", new ChannelDuplexHandler() {

                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    Packet rawPacket = (Packet) msg;

                    if (rawPacket instanceof PacketPlayOutPlayerInfo) {
                        PacketPlayOutPlayerInfo packet = (PacketPlayOutPlayerInfo) rawPacket;
                        Player player = p.playerConnection.getPlayer();
                        if (player != null) {
                            TabList list = tabLists.get(player.getName());
                            if (list != null) {
                                try {
                                    Field a = PacketPlayOutPlayerInfo.class.getDeclaredField("a");
                                    a.setAccessible(true);
                                    Field c = PacketPlayOutPlayerInfo.class.getDeclaredField("c");
                                    c.setAccessible(true);
                                    int ping = (Integer)(c.get(packet));
                                    if (ping == -1) {
                                        a.set(packet, ((String) a.get(packet)).length() > 16 ? ((String) a.get(packet)).substring(0, 16) : ((String) a.get(packet)));
                                        ping = list.getDefaultPing();
                                        for(int i=0; i<60; i++){
                                            TabSlot slot = list.getSlot(i);
                                            if(slot != null && slot.getName().equals((String)(a.get(packet)))){
                                                ping = slot.getPing();
                                                break;
                                            }
                                        }
                                        c.set(packet, ping);
                                        super.write(ctx, packet, promise);
                                        return;
                                    }
                                } catch (NoSuchFieldException ex) {
                                } catch (SecurityException ex) {
                                } catch (IllegalArgumentException ex) {
                                } catch (IllegalAccessException ex) {
                                }
                            }
                        }
                        return;
                    }
                    super.write(ctx, rawPacket, promise);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterPlayer(Player player){
        channels.get(player.getName()).pipeline().remove("TabAPI");
    }

    public PacketPlayOutScoreboardTeam buildTeamPacket(String name, String display, String prefix, String suffix, int flag, String... members) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        try {
            Field a = PacketPlayOutScoreboardTeam.class.getDeclaredField("a");
            a.setAccessible(true);
            Field b = PacketPlayOutScoreboardTeam.class.getDeclaredField("b");
            b.setAccessible(true);
            Field c = PacketPlayOutScoreboardTeam.class.getDeclaredField("c");
            c.setAccessible(true);
            Field d = PacketPlayOutScoreboardTeam.class.getDeclaredField("d");
            d.setAccessible(true);
            Field e = PacketPlayOutScoreboardTeam.class.getDeclaredField("e");
            e.setAccessible(true);
            Field f = PacketPlayOutScoreboardTeam.class.getDeclaredField("f");
            f.setAccessible(true);
            Field g = PacketPlayOutScoreboardTeam.class.getDeclaredField("g");
            g.setAccessible(true);

            a.set(packet, name.length() > 16 ? name.substring(0, 16) : name);
            f.set(packet, flag);
            if (flag == 0 || flag == 2) {

                if (display == null) {
                    b.set(packet, "");
                } else if (display.length() > 16) {
                    b.set(packet, display.substring(0, 16));
                } else {
                    b.set(packet, display);
                }

                if (prefix == null) {
                    c.set(packet, "");
                } else if (prefix.length() > 16) {
                    c.set(packet, prefix.substring(0, 16));
                } else {
                    c.set(packet, prefix);
                }

                if (suffix == null) {
                    d.set(packet, "");
                } else if (suffix.length() > 16) {
                    d.set(packet, suffix.substring(0, 16));
                } else {
                    d.set(packet, suffix);
                }

                g.set(packet, 0);
                if (flag == 0) {
                    e.set(packet, Arrays.asList(members));
                }
            }
        } catch (Exception e) {
        }
        return packet;
    }

}
