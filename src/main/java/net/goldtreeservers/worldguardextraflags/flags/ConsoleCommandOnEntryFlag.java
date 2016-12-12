package net.goldtreeservers.worldguardextraflags.flags;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

public class ConsoleCommandOnEntryFlag extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<ConsoleCommandOnEntryFlag>
    {
        @Override
        public ConsoleCommandOnEntryFlag create(Session session)
        {
            return new ConsoleCommandOnEntryFlag(session);
        }
    }
	    
	protected ConsoleCommandOnEntryFlag(Session session)
	{
		super(session);
	}

	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		for(ProtectedRegion region : entered)
		{
			Set<String> commands = region.getFlag(WorldGuardExtraFlagsPlugin.consoleCommandOnEntry);
			if (commands != null)
			{
				for(String command : commands)
				{
					WorldGuardExtraFlagsPlugin.getPlugin().getServer().dispatchCommand(WorldGuardExtraFlagsPlugin.getPlugin().getServer().getConsoleSender(), command.substring(1).replace("%username%", player.getName()));
				}
			}
		}
		return true;
	}
}
