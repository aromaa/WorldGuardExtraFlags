package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.flags.data.PotionEffectDetails;

public class BlockedEffectsFlagHandler extends FlagValueChangeHandler<Set<PotionEffectType>>
{
	public static final Factory FACTORY()
	{
		return new Factory();
	}
	
    public static class Factory extends Handler.Factory<BlockedEffectsFlagHandler>
    {
		@Override
        public BlockedEffectsFlagHandler create(Session session)
        {
            return new BlockedEffectsFlagHandler(session);
        }
    }
	
	private HashMap<PotionEffectType, PotionEffectDetails> removedEffects;
    
	protected BlockedEffectsFlagHandler(Session session)
	{
		super(session, Flags.BLOCKED_EFFECTS);
		
		this.removedEffects = new HashMap<>();
	}

	@Override
	protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Set<PotionEffectType> value)
	{
		this.handleValue(player, player.getWorld(), value);
	}

	@Override
	protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<PotionEffectType> currentValue, Set<PotionEffectType> lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) to.getExtent(), currentValue);
		return true;
	}

	@Override
	protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<PotionEffectType> lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) to.getExtent(), null);
		return true;
	}

	@Override
	public void tick(LocalPlayer player, ApplicableRegionSet set)
	{
		this.handleValue(player, player.getWorld(), set.queryValue(player, Flags.BLOCKED_EFFECTS));
	}
	
	private void handleValue(LocalPlayer player, World world, Set<PotionEffectType> value)
	{
		Player bukkitPlayer = ((BukkitPlayer) player).getPlayer();

		if (!this.getSession().getManager().hasBypass(player, world) && value != null)
		{
			for (PotionEffectType effectType : value)
			{
				PotionEffect effect = null;
				for(PotionEffect activeEffect : bukkitPlayer.getActivePotionEffects())
				{
					if (activeEffect.getType().equals(effectType))
					{
						effect = activeEffect;
						break;
					}
				}
				
				if (effect != null)
				{
					this.removedEffects.put(effect.getType(), new PotionEffectDetails(System.nanoTime() + (long)(effect.getDuration() / 20D * TimeUnit.SECONDS.toNanos(1L)), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()));

					bukkitPlayer.removePotionEffect(effectType);
				}
			}
		}
		
		Iterator<Entry<PotionEffectType, PotionEffectDetails>>  potionEffects_ = this.removedEffects.entrySet().iterator();
		while (potionEffects_.hasNext())
		{
			Entry<PotionEffectType, PotionEffectDetails> potionEffect = potionEffects_.next();
			
			if (value == null || !value.contains(potionEffect.getKey()))
			{
				PotionEffectDetails removedEffect = potionEffect.getValue();
				if (removedEffect != null)
				{
					int timeLeft = removedEffect.getTimeLeftInTicks();
					if (timeLeft > 0)
					{
						bukkitPlayer.addPotionEffect(new PotionEffect(potionEffect.getKey(), timeLeft, removedEffect.getAmplifier(), removedEffect.isAmbient(), removedEffect.isParticles()), true);
					}
				}
				
				potionEffects_.remove();
			}
		}
	}
}
