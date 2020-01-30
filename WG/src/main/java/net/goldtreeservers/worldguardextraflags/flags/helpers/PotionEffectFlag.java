package net.goldtreeservers.worldguardextraflags.flags.helpers;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class PotionEffectFlag extends Flag<PotionEffect>
{
	//This is in ticks
	//So 20 * 15 gives us 15s of the potion effect
	//This avoid the effect running out indication
	//Also we add extra 19 ticks (almost a second) to avoid the timer constantly going from 15s to 14s and back (Its annoying)
	private static final int POTION_EFFECT_DURATION = 20 * 15 + 19;
	
	public PotionEffectFlag(String name)
	{
		super(name);
	}

	@Override
	public Object marshal(PotionEffect o)
	{
		return o.getType().getName() + " " + o.getAmplifier() + " " + o.hasParticles();
	}

	@Override
	public PotionEffect parseInput(FlagContext context) throws InvalidFlagFormat
	{
		String[] split = context.getUserInput().trim().split(" ");
		if (split.length < 1 || split.length > 3)
		{
			throw new InvalidFlagFormat("Please use the following format: <effect name> [effect amplifier] [show particles]");
		}
		
		PotionEffectType potionEffect = PotionEffectType.getByName(split[0]);
		if (potionEffect == null)
		{
			throw new InvalidFlagFormat("Unable to find the potion effect type! Please refer to https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html");
		}
		
		return this.buildPotionEffect(split);
	}

	@Override
	public PotionEffect unmarshal(Object o)
	{
		String[] split = o.toString().split(" ");
		
		return this.buildPotionEffect(split);
	}
	
	private PotionEffect buildPotionEffect(String[] split)
	{
		PotionEffectType potionEffect = PotionEffectType.getByName(split[0]);
		
		int amplifier = 0;
		if (split.length >= 2)
		{
			amplifier = Integer.parseInt(split[1]);
		}
		
		boolean showParticles = false;
		if (split.length >= 3)
		{
			showParticles = Boolean.parseBoolean(split[2]);
		}
		
		return new PotionEffect(potionEffect, PotionEffectFlag.POTION_EFFECT_DURATION, amplifier, true, showParticles);
	}
}
