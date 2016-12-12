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
	    
	protected CommandOnEntryFlag(Session session)
	{
		super(session);
	}

	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		for(ProtectedRegion region : entered)
		{
			Set<String> commands = region.getFlag(WorldGuardExtraFlagsPlugin.commandOnEntry);
			if (commands != null)
			{
				for(String command : commands)
				{
					boolean isOp = player.isOp();
					
					try
					{
						player.setOp(true);
						WorldGuardExtraFlagsPlugin.getPlugin().getServer().dispatchCommand(player, command.substring(1).replace("%username%", player.getName()));
					}
					finally
					{
						player.setOp(isOp);
					}
				}
			}
		}
		return true;
	}
}
