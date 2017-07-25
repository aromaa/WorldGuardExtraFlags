package net.goldtreeservers.worldguardextraflags.flags.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;
import net.goldtreeservers.worldguardextraflags.utils.WorldGuardUtils;

public class CommandOnExitFlag extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<CommandOnExitFlag>
    {
        @Override
        public CommandOnExitFlag create(Session session)
        {
            return new CommandOnExitFlag(session);
        }
    }
	
	private Collection<Set<String>> lastCommands;
	    
	protected CommandOnExitFlag(Session session)
	{
		super(session);
		
		this.lastCommands = new ArrayList<>();
	}
	
    @Override
    public void initialize(Player player, Location current, ApplicableRegionSet set)
    {
    	this.lastCommands = set.queryAllValues(WorldGuardUtils.wrapPlayer(player), FlagUtils.COMMAND_ON_EXIT);
    }
    	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		if (!WorldGuardUtils.hasBypass(player))
		{
			Collection<Set<String>> commands = new ArrayList<Set<String>>(toSet.queryAllValues(WorldGuardUtils.wrapPlayer(player), FlagUtils.COMMAND_ON_EXIT));
			
			if (!commands.isEmpty())
			{
				for (ProtectedRegion region : toSet)
				{
	                Set<String> commands_ = region.getFlag(FlagUtils.COMMAND_ON_EXIT);
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
						WorldGuardExtraFlagsPlugin.getPlugin().getServer().dispatchCommand(player, command.substring(1).replace("%username%", player.getName())); //TODO: Make this better
					}
				}
			}
			
			this.lastCommands = commands;
		}
		
		return true;
	}
}
