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

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class GlideFlagHandler extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<GlideFlagHandler>
    {
        @Override
        public GlideFlagHandler create(Session session)
        {
            return new GlideFlagHandler(session);
        }
    }
    
    private Boolean originalGlide;
    
	protected GlideFlagHandler(Session session)
	{
		super(session);
	}
	
	@Override
    public void initialize(LocalPlayer localPlayer, Location current, ApplicableRegionSet set)
	{
		Player player = ((BukkitPlayer)localPlayer).getPlayer();
		State state = WorldGuardUtils.queryState(player, BukkitAdapter.adapt(current).getWorld(), set.getRegions(), Flags.GLIDE);
		this.handleValue(player, state);
	}
	
	@Override
	public boolean onCrossBoundary(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		Player player = ((BukkitPlayer)localPlayer).getPlayer();
		State state = WorldGuardUtils.queryState(player, BukkitAdapter.adapt(to).getWorld(), toSet.getRegions(), Flags.GLIDE);
		this.handleValue(player, state);
		
		return true;
	}
	
	private void handleValue(Player player, State state)
	{
		if (state != null)
		{
			boolean value = (state == State.ALLOW ? true : false);
			
			if (player.isGliding() != value)
			{
				if (this.originalGlide == null)
				{
					this.originalGlide = player.isGliding();
				}
				
				player.setGliding(value);
			}
		}
		else
		{
			if (this.originalGlide != null)
			{
				player.setGliding(this.originalGlide);
				
				this.originalGlide = null;
			}
		}
	}
}
