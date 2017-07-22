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

public class CommandOnEntryFlag extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<CommandOnEntryFlag>
    {
        @Override
        public CommandOnEntryFlag create(Session session)
        {
            return new CommandOnEntryFlag(session);
        }
    }
	
	private Collection<Set<String>> lastCommands;
	    
	protected CommandOnEntryFlag(Session session)
	{
		super(session);
		
		this.lastCommands = new ArrayList<>();
	}

	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		if (!WorldGuardUtils.hasBypass(player))
		{
			Collection<Set<String>> commands = toSet.queryAllValues(WorldGuardUtils.wrapPlayer(player), FlagUtils.COMMAND_ON_ENTRY);

			for(Set<String> commands_ : commands)
			{
				if (!this.lastCommands.contains(commands_))
				{
					boolean isOp = player.isOp();
					
					try
					{
						if (!isOp)
						{
							player.setOp(true);
						}
						
						for(String command : commands_)
						{
							WorldGuardExtraFlagsPlugin.getPlugin().getServer().dispatchCommand(player, command.substring(1).replace("%username%", player.getName())); //TODO: Make this better
						}
					}
					finally
					{
						if (!isOp)
						{
							player.setOp(isOp);
						}
					}
					
					break;
				}
			}
			
			this.lastCommands = new ArrayList<Set<String>>(commands);
			
			if (!this.lastCommands.isEmpty())
			{
				for (ProtectedRegion region : toSet)
				{
	                Set<String> commands_ = region.getFlag(FlagUtils.COMMAND_ON_ENTRY);
	                if (commands_ != null)
	                {
	                	this.lastCommands.add(commands_);
	                }
	            }
			}
		}
		
		return true;
	}
}
