package net.goldtreeservers.worldguardextraflags.wg.handlers;

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
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.HandlerWrapper;

public class ConsoleCommandOnEntryFlagHandler extends HandlerWrapper
{
	public static final Factory FACTORY = new Factory();
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
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		// Ignore Player NPC's
		if (player.hasMetadata("NPC"))
		{
			return true;
		}

		Collection<Set<String>> commands = WorldGuardUtils.queryAllValues(player, to.getWorld(), toSet.getRegions(), Flags.CONSOLE_COMMAND_ON_ENTRY);

		for(Set<String> commands_ : commands)
		{
			if (!this.lastCommands.contains(commands_))
			{
				for(String command : commands_)
				{
					WorldGuardExtraFlagsPlugin.getPlugin().getServer().dispatchCommand(WorldGuardExtraFlagsPlugin.getPlugin().getServer().getConsoleSender(), command.substring(1).replace("%username%", player.getName())); //TODO: Make this better
				}
				
				break;
			}
		}
		
		this.lastCommands = new ArrayList<Set<String>>(commands);
		
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
