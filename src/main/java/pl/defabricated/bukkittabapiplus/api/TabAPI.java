package pl.defabricated.bukkittabapiplus.api;

import org.bukkit.entity.Player;
import pl.defabricated.bukkittabapiplus.TabPlugin;

public class TabAPI {

    static TabPlugin plugin;

    public TabAPI(TabPlugin tabPlugin){
        plugin = tabPlugin;
    }

    public static TabList createTabListForPlayer(Player player){
        TabList list = new TabList(plugin, player);
        plugin.tabLists.put(player.getName(), list);
        return list;
    }

    public static TabList getPlayerTabList(Player player){
        return plugin.tabLists.get(player.getName());
    }

}
