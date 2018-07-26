package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.flags.data.SoundData;
import net.goldtreeservers.worldguardextraflags.utils.SupportedFeatures;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class PlaySoundsFlagHandler extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<PlaySoundsFlagHandler>
    {
        @Override
        public PlaySoundsFlagHandler create(Session session)
        {
            return new PlaySoundsFlagHandler(session);
        }
    }

    private Map<String, BukkitRunnable> runnables;
	    
	protected PlaySoundsFlagHandler(Session session)
	{
		super(session);
		
		this.runnables = new HashMap<>();
	}

	@Override
	public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set)
	{
		this.check(player, set);
    }
	
	@Override
	public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		this.check(player, toSet);
		
		return true;
	}

	@Override
	public void tick(LocalPlayer player, ApplicableRegionSet set)
	{
		this.check(player, set);
    }
	
	private void check(LocalPlayer localPlayer, ApplicableRegionSet set)
	{
		Player player = ((BukkitPlayer)localPlayer).getPlayer();
		Set<SoundData> soundData = WorldGuardUtils.queryValue(player, player.getWorld(), set.getRegions(), Flags.PLAY_SOUNDS);
		if (soundData != null && soundData.size() > 0)
		{
			for(SoundData sound : soundData)
			{
				if (!this.runnables.containsKey(sound.getSound()))
				{
					BukkitRunnable runnable = new BukkitRunnable()
					{
						@Override
						public void run()
						{
							player.playSound(player.getLocation(), sound.getSound(), Float.MAX_VALUE, 1);
						}
						
						@Override
						public void cancel()
						{
							super.cancel();
							
							if (SupportedFeatures.isStopSoundSupported())
							{
								player.stopSound(sound.getSound());
							}
						}
					};
	
					this.runnables.put(sound.getSound(), runnable);
					runnable.runTaskTimer(WorldGuardExtraFlagsPlugin.getPlugin(), 0L, sound.getInterval());
				}
			}
		}
		
		Iterator<Entry<String, BukkitRunnable>> runnables = this.runnables.entrySet().iterator();
		while (runnables.hasNext())
		{
			Entry<String, BukkitRunnable> runnable = runnables.next();
			
			if (soundData != null && soundData.size() > 0)
			{
				boolean skip = false;
				for(SoundData sound : soundData)
				{
					if (sound.getSound().equals(runnable.getKey()))
					{
						skip = true;
						break;
					}
				}
				
				if (skip)
				{
					continue;
				}
			}
			
			runnable.getValue().cancel();
			
			runnables.remove();
		}
	}
}
