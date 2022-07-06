package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.flags.data.SoundData;

public class PlaySoundsFlagHandler extends FlagValueChangeHandler<Set<SoundData>>
{
	public static final Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
    public static class Factory extends Handler.Factory<PlaySoundsFlagHandler>
    {
		private final Plugin plugin;

		public Factory(Plugin plugin)
		{
			this.plugin = plugin;
		}

		@Override
        public PlaySoundsFlagHandler create(Session session)
        {
            return new PlaySoundsFlagHandler(this.plugin, session);
        }
    }

	private final Plugin plugin;
    private Map<String, BukkitRunnable> runnables;
	    
	protected PlaySoundsFlagHandler(Plugin plugin, Session session)
	{
		super(session, Flags.PLAY_SOUNDS);

		this.plugin = plugin;
		
		this.runnables = new HashMap<>();
	}

	@Override
	protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Set<SoundData> value)
	{
		this.handleValue(player, value);
	}

	@Override
	protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<SoundData> currentValue, Set<SoundData> lastValue, MoveType moveType)
	{
		this.handleValue(player, currentValue);
		return true;
	}

	@Override
	protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<SoundData> lastValue, MoveType moveType)
	{
		this.handleValue(player, null);
		return true;
	}

	@Override
	public void tick(LocalPlayer player, ApplicableRegionSet set)
	{
		this.handleValue(player, set.queryValue(player, Flags.PLAY_SOUNDS));
    }
	
	private void handleValue(LocalPlayer player, Set<SoundData> value)
	{
		Player bukkitPlayer = ((BukkitPlayer) player).getPlayer();

		if (value != null && value.size() > 0)
		{
			for(SoundData sound : value)
			{
				if (!this.runnables.containsKey(sound.sound()))
				{
					BukkitRunnable runnable = new BukkitRunnable()
					{
						@Override
						public void run()
						{
							bukkitPlayer.playSound(bukkitPlayer.getLocation(), sound.sound(), sound.source(), sound.volume(), sound.pitch());
						}
						
						@Override
						public void cancel()
						{
							super.cancel();

							bukkitPlayer.stopSound(sound.sound(), sound.source());
						}
					};
	
					this.runnables.put(sound.sound(), runnable);
					
					runnable.runTaskTimer(this.plugin, 0L, sound.interval());
				}
			}
		}
		
		Iterator<Entry<String, BukkitRunnable>> runnables = this.runnables.entrySet().iterator();
		while (runnables.hasNext())
		{
			Entry<String, BukkitRunnable> runnable = runnables.next();
			
			if (value != null && value.size() > 0)
			{
				boolean skip = false;
				for(SoundData sound : value)
				{
					if (sound.sound().equals(runnable.getKey()))
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
