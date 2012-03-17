package me.kalmanolah.okb3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class OKConfig
{
    public static HashMap<String, Object> config = new HashMap<String, Object>();
    static String directory = "plugins" + File.separator + OKmain.name;
    static File file = new File(directory + File.separator + "config.yml");

    private static OKmain plugin;

    public OKConfig(OKmain thePlugin)
    {
        plugin = thePlugin;
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        if (plugin.getConfig().getString("general.mode").equalsIgnoreCase("secure"))
        {
            config.put("mode", 1);
        }
        else
        {
            config.put("mode", 0);
        }
        config.put("gen.whitelist", plugin.getConfig().getBoolean("general.enable-whitelist"));
        config.put("gen.nicks", plugin.getConfig().getBoolean("general.enable-nickname-syncing"));
        config.put("gen.bans", plugin.getConfig().getBoolean("general.enable-synced-banning"));
        config.put("gen.track", plugin.getConfig().getBoolean("general.enable-promotion-track"));
        config.put("gen.ranks", plugin.getConfig().getBoolean("general.enable-rank-changing"));
        config.put("gen.posts", plugin.getConfig().getBoolean("general.enable-post-count-rewarding"));
        config.put("mysql.host", plugin.getConfig().getString("mysql-connection.mysql-host"));
        config.put("mysql.user", plugin.getConfig().getString("mysql-connection.mysql-user"));
        config.put("mysql.pass", plugin.getConfig().getString("mysql-connection.mysql-password"));
        config.put("mysql.db", plugin.getConfig().getString("mysql-connection.mysql-database-name"));
        config.put("mysql.port", plugin.getConfig().getInt("mysql-connection.mysql-port"));

        HashMap<String, String> groupmap = new HashMap<String, String>();
        List<String> groups = plugin.getConfig().getStringList("group-mapping.default");
        Iterator<String> group = groups.iterator();
        while (group.hasNext())
        {
            String nextgroup = group.next();
            groupmap.put(nextgroup, plugin.getConfig().getString("group-mapping.default." + nextgroup));
        }
        config.put("groups", groupmap);
        List<String> worlds = plugin.getConfig().getStringList("group-mapping.worlds");
        if (worlds != null && !worlds.isEmpty())
        {
            Iterator<String> world = worlds.iterator();
            while (world.hasNext())
            {
                String nextworld = world.next();
                HashMap<String, String> worldmap = new HashMap<String, String>();
                List<String> worldgroups = plugin.getConfig().getStringList("group-mapping.worlds." + nextworld);
                Iterator<String> worldgroup = worldgroups.iterator();
                while (worldgroup.hasNext())
                {
                    String nextworldgroup = worldgroup.next();
                    worldmap.put(nextworldgroup, plugin.getConfig().getString("group-mapping.worlds." + nextworld + "." + nextworldgroup));
                }
                config.put("groups." + nextworld, worldmap);
            }
        }
        if ((Boolean) config.get("gen.whitelist"))
        {
            config.put("whitelist.message", plugin.getConfig().getString("extras.whitelist.kick-message"));
            config.put("whitelist.blacklist", Boolean.parseBoolean(plugin.getConfig().getString("extras.whitelist.use-as-blacklist")));
            HashMap<String, Boolean> whitelistmap = new HashMap<String, Boolean>();
            List<String> whitelistgroups = plugin.getConfig().getStringList("extras.whitelist.groups");
            Iterator<String> whitelistgroup = whitelistgroups.iterator();
            while (whitelistgroup.hasNext())
            {
                String nextgroup = whitelistgroup.next();
                whitelistmap.put(nextgroup, Boolean.parseBoolean(plugin.getConfig().getString("extras.whitelist.groups." + nextgroup)));
            }
            config.put("whitelist.groups", whitelistmap);
        }
        if ((Boolean) config.get("gen.nicks"))
        {
            config.put("nicks.field", plugin.getConfig().getString("extras.nickname-syncing.nickname-field-in-usertable"));
        }
        if ((Boolean) config.get("gen.bans"))
        {
            config.put("bans.banrank", plugin.getConfig().getString("extras.synced-banning.banned-user-forum-rank-id"));
            config.put("bans.unbanrank", plugin.getConfig().getString("extras.synced-banning.unbanned-user-forum-rank-id"));
            config.put("bans.message", plugin.getConfig().getString("extras.synced-banning.ban-message"));
        }
        if ((Boolean) config.get("gen.track"))
        {
            String[] track = plugin.getConfig().getString("extras.promotion-track.track").split("\\|");
            List<String> promotiontrack = new ArrayList<String>();
            for (String t : track)
            {
                promotiontrack.add(t);
            }
            config.put("track.track", promotiontrack);
            HashMap<String, String> identifiers = new HashMap<String, String>();
            HashMap<String, String> identifierstwo = new HashMap<String, String>();
            config.put("ranks.identifiers", identifiers);
            config.put("ranks.identifierstwo", identifierstwo);
        }
        if ((Boolean) config.get("gen.ranks"))
        {
            List<String> rankidentifiers = plugin.getConfig().getStringList("extras.rank-changing.identifiers");
            Iterator<String> rankidentifier = rankidentifiers.iterator();
            HashMap<String, String> identifiers = new HashMap<String, String>();
            HashMap<String, String> identifierstwo = new HashMap<String, String>();
            while (rankidentifier.hasNext())
            {
                String nextidentifier = rankidentifier.next();
                identifiers.put(plugin.getConfig().getString("extras.rank-changing.identifiers." + nextidentifier).toLowerCase(), nextidentifier);
                identifierstwo.put(nextidentifier, plugin.getConfig().getString("extras.rank-changing.identifiers." + nextidentifier).toLowerCase());
            }
            config.put("ranks.identifiers", identifiers);
            config.put("ranks.identifierstwo", identifierstwo);
        }
        if ((Boolean) config.get("gen.posts"))
        {
            config.put("posts.field", plugin.getConfig().getString("extras.post-count-rewarding.post-count-field-in-user-table"));
            config.put("posts.reward", Double.parseDouble(plugin.getConfig().getString("extras.post-count-rewarding.currency-reward-per-post")));
        }
    }
}