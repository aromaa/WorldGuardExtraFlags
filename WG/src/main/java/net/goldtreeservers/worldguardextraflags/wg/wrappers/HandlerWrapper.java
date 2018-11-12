package net.goldtreeservers.worldguardextraflags.wg.wrappers;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

public abstract class HandlerWrapper extends Handler
{
	protected HandlerWrapper(Session session)
	{
		super(session);
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
}
