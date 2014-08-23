package pl.defabricated.bukkittabapiplus.api;

import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

public class TabSlot {

    TabSlot(TabList list, String prefix, String name, String suffix){
        this.list = list;

        this.prefix = prefix;
        this.name = name;
        this.suffix = suffix;

        this.teamExists = true;
        this.sent = false;
    }

    TabSlot(TabList list, String name){
        this.list = list;

        this.name = name;

        this.teamExists = false;
        this.sent = false;
    }

    TabList list;
    boolean sent, teamExists, toRemove;

    private boolean cPrefix, cSuffix;
    String oldName;
    String prefix, name, suffix;
    private int ping;

    public void setPrefix(String prefix){ cPrefix = true; this.prefix = prefix; }
    public void setName(String name){ this.oldName = this.name; this.name = name; }
    public void setSuffix(String suffix){ cSuffix = true; this.suffix = suffix; }
    public void setPing(int ping){ this.ping = ping; }

    public String getPrefix(){ return prefix; }
    public String getName(){ return name; }
    public String getSuffix(){ return suffix; }
    public int getPing(){ return ping; }

    public void createPrefixAndSuffix(String prefix, String suffix){
        if(toRemove || teamExists){ //2 teams with the same name causes client crash
            return;
        }

        this.teamExists = true;

        this.prefix = prefix;
        this.suffix = suffix;

        PacketPlayOutScoreboardTeam packet = list.plugin.buildTeamPacket(name, name, prefix, suffix, 0, name);
        ((CraftPlayer)list.player).getHandle().playerConnection.sendPacket(packet);
    }

    public void updatePrefixAndSuffix(String prefix, String suffix){
        if(toRemove || !teamExists){ //Updating prefix and suffix of team which doesn't exists causes client crash
            return;
        }

        this.prefix = prefix;
        this.suffix = suffix;

        PacketPlayOutScoreboardTeam packet = list.plugin.buildTeamPacket(name, name, prefix, suffix, 2, name);
        ((CraftPlayer)list.player).getHandle().playerConnection.sendPacket(packet);
    }

    public void removePrefixAndSuffix(String prefix, String suffix){
        if(toRemove || !teamExists){ //Removing team which doesn't exists causes client crash
            return;
        }

        this.teamExists = false;

        this.prefix = prefix;
        this.suffix = suffix;

        PacketPlayOutScoreboardTeam packet = list.plugin.buildTeamPacket(name, name, prefix, suffix, 1, name);
        ((CraftPlayer)list.player).getHandle().playerConnection.sendPacket(packet);
    }

}
