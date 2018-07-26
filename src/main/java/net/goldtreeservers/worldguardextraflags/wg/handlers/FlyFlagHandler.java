package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.Set;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import lombok.Getter;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class FlyFlagHandler extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<FlyFlagHandler>
    {
        @Override
        public FlyFlagHandler create(Session session)
        {
            return new FlyFlagHandler(session);
        }
    }

    @Getter private Boolean currentValue;
    private Boolean originalFly;
	    
	protected FlyFlagHandler(Session session)
	{
		super(session);
	}
	
	@Override
    public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set)
	{
		State state = WorldGuardUtils.queryState(((BukkitPlayer)player).getPlayer(), BukkitAdapter.adapt(current).getWorld(), set.getRegions(), Flags.FLY);
		this.handleValue(player, state);
	}
	
	@Override
	public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		State state = WorldGuardUtils.queryState(((BukkitPlayer)player).getPlayer(), BukkitAdapter.adapt(to).getWorld(), toSet.getRegions(), Flags.FLY);
		this.handleValue(player, state);
		
		return true;
	}
	
	private void handleValue(LocalPlayer localPlayer, State state)
	{
		Player player = ((BukkitPlayer)localPlayer).getPlayer();
		if (state != null)
		{
			boolean value = (state == State.ALLOW ? true : false);
			
			if (player.getAllowFlight() != value)
			{
				if (this.originalFly == null)
				{
					this.originalFly = player.getAllowFlight();
				}
				
				player.setAllowFlight(value);
			}
		}
		else
		{
			if (this.originalFly != null)
			{
				player.setAllowFlight(this.originalFly);
				
				this.originalFly = null;
			}
		}
	}
}
