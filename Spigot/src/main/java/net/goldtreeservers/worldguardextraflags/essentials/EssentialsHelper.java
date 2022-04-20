package net.goldtreeservers.worldguardextraflags.essentials;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.listeners.EssentialsListener;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class EssentialsHelper
{
	@Getter private final WorldGuardExtraFlagsPlugin plugin;
	@Getter private final Plugin essentialsPlugin;

	@Getter private final RegionContainer regionContainer;
	@Getter private final SessionManager sessionManager;
	
	public void onEnable()
	{
		this.plugin.getServer().getPluginManager().registerEvents(new EssentialsListener(this.plugin, (Essentials) this.essentialsPlugin, this.regionContainer, this.sessionManager), this.plugin);
	}
}
