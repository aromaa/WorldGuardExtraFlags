package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.flags.data.PotionEffectDetails;
import net.goldtreeservers.worldguardextraflags.utils.SupportedFeatures;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class BlockedEffectsFlagHandler extends Handler
{
	public static final Factory FACTORY = new Factory();
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
		super(session);
		
		this.removedEffects = new HashMap<>();
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
		Set<PotionEffectType> potionEffects = WorldGuardUtils.queryValue(player, player.getWorld(), set.getRegions(), Flags.BLOCKED_EFFECTS);
		if (potionEffects != null && potionEffects.size() > 0)
		{
			for (PotionEffectType effectType : potionEffects)
			{
				PotionEffect effect = null;
				for(PotionEffect activeEffect : player.getActivePotionEffects())
				{
					if (activeEffect.getType().equals(effectType))
					{
						effect = activeEffect;
						break;
					}
				}
				
				if (effect != null)
				{
					this.removedEffects.put(effect.getType(), new PotionEffectDetails(System.nanoTime() + (long)(effect.getDuration() / 20D * TimeUnit.SECONDS.toNanos(1L)), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), SupportedFeatures.isMobEffectColorsSupported() ? effect.getColor() : null));
					
					player.removePotionEffect(effectType);
				}
			}
		}
		
		Iterator<Entry<PotionEffectType, PotionEffectDetails>>  potionEffects_ = this.removedEffects.entrySet().iterator();
		while (potionEffects_.hasNext())
		{
			Entry<PotionEffectType, PotionEffectDetails> potionEffect = potionEffects_.next();
			
			if (potionEffects == null || !potionEffects.contains(potionEffect.getKey()))
			{
				PotionEffectDetails removedEffect = potionEffect.getValue();
				if (removedEffect != null)
				{
					int timeLeft = removedEffect.getTimeLeftInTicks();
					if (timeLeft > 0)
					{
						if (SupportedFeatures.isMobEffectColorsSupported())
						{
							player.addPotionEffect(new PotionEffect(potionEffect.getKey(), timeLeft, removedEffect.getAmplifier(), removedEffect.isAmbient(), removedEffect.isParticles(), removedEffect.getColor()), true);
						}
						else
						{
							player.addPotionEffect(new PotionEffect(potionEffect.getKey(), timeLeft, removedEffect.getAmplifier(), removedEffect.isAmbient(), removedEffect.isParticles()), true);
						}
					}
				}
				
				potionEffects_.remove();
			}
		}
	}
}
