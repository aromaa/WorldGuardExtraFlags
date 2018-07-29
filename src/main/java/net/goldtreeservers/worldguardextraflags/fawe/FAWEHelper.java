package net.goldtreeservers.worldguardextraflags.fawe;

import org.bukkit.plugin.Plugin;

import com.boydti.fawe.FaweAPI;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

@RequiredArgsConstructor
public class FAWEHelper
{
	@Getter private final WorldGuardExtraFlagsPlugin plugin;
	@Getter private final Plugin fawePlugin;
	
	public void onEnable()
	{
		FaweAPI.addMaskManager(new FaweWorldEditFlagMaskManager(this.plugin));
	}
}
