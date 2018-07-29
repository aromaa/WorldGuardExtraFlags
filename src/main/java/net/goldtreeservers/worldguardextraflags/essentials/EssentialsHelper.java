package net.goldtreeservers.worldguardextraflags.essentials;

import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.listeners.EssentialsListener;

@RequiredArgsConstructor
public class EssentialsHelper
{
	@Getter private final WorldGuardExtraFlagsPlugin plugin;
	@Getter private final Essentials essentialsPlugin;
	
	public EssentialsHelper(WorldGuardExtraFlagsPlugin plugin, Plugin essentialsPlugin)
	{
		this(plugin, (Essentials)essentialsPlugin);
	}
	
	public void onEnable()
	{
		this.plugin.getServer().getPluginManager().registerEvents(new EssentialsListener(this.plugin, this.essentialsPlugin), this.plugin);
	}
}
