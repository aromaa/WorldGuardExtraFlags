package net.goldtreeservers.worldguardextraflags.essentials;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
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
		WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();

		this.plugin.getServer().getPluginManager().registerEvents(new EssentialsListener(this.plugin, this.essentialsPlugin, platform.getRegionContainer(), platform.getSessionManager()), this.plugin);
	}
}
