package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;

public class ConsoleCommandOnEntryFlagHandler extends Handler
{
	public static final Factory FACTORY()
	{
		return new Factory();
	}
	
    public static class Factory extends Handler.Factory<ConsoleCommandOnEntryFlagHandler>
    {
		@Override
        public ConsoleCommandOnEntryFlagHandler create(Session session)
        {
            return new ConsoleCommandOnEntryFlagHandler(session);
        }
    }
    
	private Collection<Set<String>> lastCommands;
	    
	protected ConsoleCommandOnEntryFlagHandler(Session session)
	{
		super(session);
		
		this.lastCommands = new ArrayList<>();
	}

	@Override
	public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		Collection<Set<String>> commands = toSet.queryAllValues(player, Flags.CONSOLE_COMMAND_ON_ENTRY);

		if (!this.getSession().getManager().hasBypass(player, (World) to.getExtent()))
		{
			for (Set<String> commands_ : commands)
			{
				if (!this.lastCommands.contains(commands_))
				{
					Plugin plugin;
					try
					{
						plugin = JavaPlugin.getProvidingPlugin(this.getClass());
					}
					catch (IllegalArgumentException | IllegalStateException e)
					{
						plugin = Bukkit.getPluginManager().getPlugin("WorldGuardExtraFlags");
					}

					if (plugin == null)
					{
						Bukkit.getLogger().severe("[WGEF-DEBUG] CRITICAL: Plugin instance is NULL! Commands cannot run.");
						break;
					}

					if (!plugin.isEnabled())
					{
						Bukkit.getLogger().warning("[WGEF-DEBUG] Plugin is disabled, skipping commands.");
						break;
					}

					final Set<String> commandsToRun = commands_;
					final String playerName = player.getName();
					final Plugin pluginFinal = plugin;

					Runnable commandTask = () ->
					{
						ConsoleCommandSender console = Bukkit.getConsoleSender();
						for (String command : commandsToRun)
						{
							try
							{
								if (command == null || command.trim().isEmpty()) continue;

								String cmdToRun = command.replace("%username%", playerName).trim();
								if (cmdToRun.startsWith("/"))
								{
									cmdToRun = cmdToRun.substring(1);
								}

								Bukkit.dispatchCommand(console, cmdToRun);
							}
							catch (Throwable t)
							{
								pluginFinal.getLogger().warning("[WGEF-Error] Failed to execute command '" + command + "' for " + playerName);
								t.printStackTrace();
							}
						}
					};

					if (Bukkit.isPrimaryThread())
					{
						commandTask.run();
					}
					else
					{
						Bukkit.getScheduler().runTask(pluginFinal, commandTask);
					}

					break;
				}
			}
		}
		
		this.lastCommands = new ArrayList(commands);
		
		if (!this.lastCommands.isEmpty())
		{
			for (ProtectedRegion region : toSet)
			{
                Set<String> commands_ = region.getFlag(Flags.CONSOLE_COMMAND_ON_ENTRY);
                if (commands_ != null)
                {
                	this.lastCommands.add(commands_);
                }
            }
		}
		
		return true;
	}
}
