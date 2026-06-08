package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import net.goldtreeservers.worldguardextraflags.flags.helpers.ForcedStateFlag;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.world.PortalCreateEvent;

import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.flags.Flags;

public class EntityListener implements Listener
{
	private final WorldGuardPlugin worldGuardPlugin;
	private final RegionContainer regionContainer;
	private final SessionManager sessionManager;

	public EntityListener(WorldGuardPlugin worldGuardPlugin, RegionContainer regionContainer, SessionManager sessionManager) {
		this.worldGuardPlugin = worldGuardPlugin;
		this.regionContainer = regionContainer;
		this.sessionManager = sessionManager;
	}

	@EventHandler(ignoreCancelled = true)
	public void onPortalCreateEvent(PortalCreateEvent event)
	{
		LocalPlayer localPlayer;
		if (event.getEntity() instanceof Player player)
		{
			localPlayer = this.worldGuardPlugin.wrapPlayer(player);
			if (this.sessionManager.hasBypass(localPlayer, localPlayer.getWorld()))
			{
				return;
			}
		}
		else
		{
			localPlayer = null;
		}

		for (BlockState block : event.getBlocks())
		{
			if (this.regionContainer.createQuery().queryState(BukkitAdapter.adapt(block.getLocation()), localPlayer, Flags.NETHER_PORTALS) == State.DENY)
			{
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityToggleGlideEvent(EntityToggleGlideEvent event)
	{
		Entity entity = event.getEntity();
		if (entity instanceof Player player)
		{
			LocalPlayer localPlayer = this.worldGuardPlugin.wrapPlayer(player);
			if (this.sessionManager.hasBypass(localPlayer, localPlayer.getWorld()))
			{
				return;
			}

			ForcedStateFlag.ForcedState state = this.regionContainer.createQuery().queryValue(localPlayer.getLocation(), localPlayer, Flags.GLIDE);
			if (state == null) return;
            switch (state) {
                case DENY -> {
                    if (!event.isGliding()) {
                        return;
                    }

                    event.setCancelled(true);

                    //Prevent the player from being allowed to glide by spamming space
                    player.teleport(player.getLocation());

                }
                case FORCE -> {
                    if (event.isGliding()) {
                        return;
                    }

                    event.setCancelled(true);
                }
            }
		}
	}
}
