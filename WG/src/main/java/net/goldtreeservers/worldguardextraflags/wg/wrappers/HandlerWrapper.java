package net.goldtreeservers.worldguardextraflags.wg.wrappers;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import lombok.Getter;

public abstract class HandlerWrapper extends Handler
{
	@Getter private final Plugin plugin;
	
	protected HandlerWrapper(Plugin plugin, Session session)
	{
		super(session);
		
		this.plugin = plugin;
	}
	
	public void initialize(Player player, Location current, ApplicableRegionSet set)
	{
	}
	
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		return true;
	}
	
	public void tick(Player player, ApplicableRegionSet set)
	{
	}

    public State getInvincibility(Player player)
    {
		return null;
    }
	
	@Override
	public void initialize(LocalPlayer localPlayer, com.sk89q.worldedit.util.Location current, ApplicableRegionSet set)
	{
		this.initialize(((BukkitPlayer)localPlayer).getPlayer(), BukkitAdapter.adapt(current), set);
	}
	
	@Override
	public boolean onCrossBoundary(LocalPlayer localPlayer, com.sk89q.worldedit.util.Location from, com.sk89q.worldedit.util.Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		//It turns out that this is fired every time player moves
		//The plugin flags assume already that nothing changes unless region is crossed, so ignore when there isn't a region change
		//This optimization is already in place in the FVCH
		if (entered.isEmpty() && exited.isEmpty() && from.getExtent().equals(to.getExtent()))
		{
            return true;
        }
		
		return this.onCrossBoundary(((BukkitPlayer)localPlayer).getPlayer(), BukkitAdapter.adapt(from), BukkitAdapter.adapt(to), toSet, entered, exited, moveType);
	}
	
	@Override
	public void tick(LocalPlayer localPlayer, ApplicableRegionSet set)
	{
		this.tick(((BukkitPlayer)localPlayer).getPlayer(), set);
	}
	
	@Override
	public State getInvincibility(LocalPlayer localPlayer)
	{
		return this.getInvincibility(((BukkitPlayer)localPlayer).getPlayer());
	}
	
	public abstract static class Factory<T extends HandlerWrapper> extends Handler.Factory<T>
	{
		@Getter private final Plugin plugin;
		
		public Factory(Plugin plugin)
		{
			this.plugin = plugin;
		}
	}
}
