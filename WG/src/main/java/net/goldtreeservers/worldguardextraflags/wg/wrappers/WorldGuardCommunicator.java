package net.goldtreeservers.worldguardextraflags.wg.wrappers;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public interface WorldGuardCommunicator
{
	public void onLoad() throws Exception;
	public void onEnable() throws Exception;
	
	public FlagRegistry getFlagRegistry();
	
	public AbstractSessionManagerWrapper getSessionManager();
	public AbstractRegionContainerWrapper getRegionContainer();
	
	public LocalPlayer wrapPlayer(Player player);
	
	public <T> SetFlag<T> getCustomSetFlag(String name, Flag<T> flag);
	
	public AbstractDelegateExtent getWorldEditFlag(World world, Extent extent, com.sk89q.worldedit.entity.Player player);
	
	public void doUnloadChunkFlagCheck(org.bukkit.World world);
	public boolean doUnloadChunkFlagCheck(org.bukkit.World world, org.bukkit.Chunk chunk);
}
