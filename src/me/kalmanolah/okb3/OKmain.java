package me.kalmanolah.okb3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import me.kalmanolah.okb3.commands.*;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.greatmancode.extras.Metrics;
import com.greatmancode.okb3.OKBSync;
import com.greatmancode.okb3.OKLogger;

public class OKmain extends JavaPlugin
{
    public static String name;
    public static String version;
    public static List<String> authors;
    public List<BaseCommand> commands = new ArrayList<BaseCommand>();
    private final OKPlayerListener playerListener = new OKPlayerListener(this);
    public static List<Player> kicks = new ArrayList<Player>();
    public static List<Player> portals = new ArrayList<Player>();
    public static HashMap<Player, String> cachedjoinmsgs = new HashMap<Player, String>();
    public static Permission perms;
    public static OKBSync sync;
    public static OKmain p;

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public void onEnable()
    {
        name = getDescription().getName();
        version = getDescription().getVersion();
        authors = getDescription().getAuthors();
        p = this;

        OKLogger.initialize(Logger.getLogger("Minecraft"));
        OKLogger.info("Attempting to enable " + name + " v" + version + " by " + authors.get(0) + "...");
        PluginManager pm = getServer().getPluginManager();
        if (!setupPermissions())
        {
            OKLogger.info("Permissions plugin not found, shutting down...");
            pm.disablePlugin(this);
        }
        else
        {
            new OKConfig(this);
            try
            {
                Class<?> that = OKBSync.class.getClassLoader().loadClass("me.kalmanolah.forumlistener." + (String) OKConfig.config.get("configuration.forum"));
                sync = (OKBSync) that.newInstance();
                OKLogger.info("Loaded " + OKConfig.config.get("configuration.forum") + " forum link");
                OKDatabase.initialize(this);
                OKDB.initialize(this);
                new OKFunctions(this);
                pm.registerEvents(playerListener, this);
                setupCommands();
                Metrics metrics = new Metrics(this);
                metrics.start();
                
                OKLogger.info(name + " v" + version + " enabled successfully.");
            }
            catch (InstantiationException e)
            {
                OKLogger.info("A error occured while loading the forum link class. ");
                pm.disablePlugin(this);
            }
            catch (IllegalAccessException e)
            {
                OKLogger.info("A error occured while loading the forum link class.");
                pm.disablePlugin(this);
            }
            catch (ClassNotFoundException e1)
            {
                OKLogger.info("Forum link class not found, shutting down.... Check if the configuration.forum configuration node is configurated correctly.");
                pm.disablePlugin(this);
            }
            catch (IOException e)
            {
                OKLogger.info("An error occured while activating Plugin stats");
            }
        }
    }

    private void setupCommands()
    {
        commands.add(new BbbCommand());
        commands.add(new BbbVersionCommand());
        commands.add(new SyncCommand());
        commands.add(new ResyncCommand());
        commands.add(new FsyncCommand());
        commands.add(new FsyncAllCommand());
        commands.add(new FBanCommand());
        commands.add(new FUnbanCommand());
        commands.add(new FPromoteCommand());
        commands.add(new FDemoteCommand());
        commands.add(new FRankCommand());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        List<String> parameters = new ArrayList<String>(Arrays.asList(args));
        String commandName = cmd.getName();
        for (BaseCommand OKBCommand : this.commands)
        {
            if (OKBCommand.getCommands().contains(commandName))
            {
                OKBCommand.execute(sender, parameters);
                return true;
            }
        }
        return false;

    }

    @SuppressWarnings("unchecked")
    public void changeGroup(String player, List<Integer> rank, String world, Boolean mode)
    {
        String groupname = null;
        HashMap<Integer, String> worldgroups;
        if (mode)
        {
            world = getServer().getPlayer(player).getWorld().getName();
            worldgroups = (HashMap<Integer, String>) OKFunctions.getConfig("groups." + world);
            if (worldgroups == null)
            {
                worldgroups = (HashMap<Integer, String>) OKFunctions.getConfig("groups");  
            }
        }
        else
        {
            worldgroups = (HashMap<Integer, String>) OKFunctions.getConfig("groups");  
        }
        if (worldgroups != null)
        {
        	Iterator<Integer> iterator = rank.iterator();
        	boolean first = true;
        	while (iterator.hasNext())
        	{
        		int theRank = iterator.next();
        		if (worldgroups.containsKey(theRank))
                {
                    groupname = worldgroups.get(theRank);
                }
                if (groupname == null)
                {
                    worldgroups = (HashMap<Integer, String>) OKFunctions.getConfig("groups");
                    groupname = worldgroups.get(theRank);
                }
                if (groupname != null)
                {
                	if (first)
                	{
                		String[] groupList = perms.getPlayerGroups(getServer().getPlayer(player));
                        for (int i = 0; i < groupList.length; i++)
                        {
                            perms.playerRemoveGroup(getServer().getPlayer(player), groupList[i]);
                        }
                        first = false;
                	}
                 
                    perms.playerAddGroup(getServer().getPlayer(player), groupname);

                }
        	}
            
        }
        else
        {
            OKLogger.info("ERROR");
        }
        
    }

    public static boolean CheckPermission(Player player, String string)
    {
        return player.hasPermission(string);
    }

    public static void kickPlayer(Player plr, String string)
    {
        plr.kickPlayer(string);
    }

    public void onDisable()
    {
        OKLogger.info("Attempting to disable " + name + "...");
        OKDatabase.disable();
        OKDB.disable();
        getServer().getScheduler().cancelTasks(this);
        OKLogger.info(name + " disabled successfully.");
    }
}