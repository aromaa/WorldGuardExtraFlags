package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.HandlerWrapper;

public class CommandOnExitFlagHandler extends HandlerWrapper
{
	public static final Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
    public static class Factory extends HandlerWrapper.Factory<CommandOnExitFlagHandler>
    {
        public Factory(Plugin plugin)
        {
			super(plugin);
		}

		@Override
        public CommandOnExitFlagHandler create(Session session)
        {
            return new CommandOnExitFlagHandler(this.getPlugin(), session);
        }
    }
	
	private Collection<Set<String>> lastCommands;
	    
	protected CommandOnExitFlagHandler(Plugin plugin, Session session)
	{
		super(plugin, session);
		
		this.lastCommands = new ArrayList<>();
	}
	
    @Override
    public void initialize(Player player, Location current, ApplicableRegionSet set)
    {
    	this.lastCommands = WorldGuardUtils.queryAllValues(player, current.getWorld(), set.getRegions(), Flags.COMMAND_ON_EXIT);
    }
    	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		Collection<Set<String>> commands = new ArrayList<Set<String>>(WorldGuardUtils.queryAllValues(player, to.getWorld(), toSet.getRegions(), Flags.COMMAND_ON_EXIT));
		
		if (!commands.isEmpty())
		{
			for (ProtectedRegion region : toSet)
			{
                Set<String> commands_ = region.getFlag(Flags.COMMAND_ON_EXIT);
                if (commands_ != null)
                {
                	commands.add(commands_);
                }
            }
		}
		
		for(Set<String> commands_ : this.lastCommands)
		{
			if (!commands.contains(commands_) && commands_.size() > 0)
			{
				for(String command : commands_)
				{
					this.getPlugin().getServer().dispatchCommand(player, command.substring(1).replace("%username%", player.getName())); //TODO: Make this better
				}
			}
		}
		
		this.lastCommands = commands;
		
		return true;
	}
}
