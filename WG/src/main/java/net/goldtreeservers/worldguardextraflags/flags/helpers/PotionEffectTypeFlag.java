package net.goldtreeservers.worldguardextraflags.flags.helpers;

import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class PotionEffectTypeFlag extends Flag<PotionEffectType>
{
	public PotionEffectTypeFlag(String name)
	{
		super(name);
	}

	@Override
	public Object marshal(PotionEffectType o)
	{
		return o.getKey().toString();
	}

	@Override
	public PotionEffectType parseInput(FlagContext context) throws InvalidFlagFormat
	{
		PotionEffectType potionEffect = Registry.EFFECT.match(context.getUserInput().trim());
		if (potionEffect == null)
		{
			potionEffect = PotionEffectType.getByName(context.getUserInput().trim());
		}

		if (potionEffect != null)
		{
			return potionEffect;
		}

		throw new InvalidFlagFormat("Unable to find the potion effect type! Input valid namespaced ids.");
	}

	@Override
	public PotionEffectType unmarshal(Object o)
	{
		PotionEffectType potionEffect = Registry.EFFECT.match(o.toString());
		if (potionEffect == null)
		{
			potionEffect = PotionEffectType.getByName(o.toString());
		}

		return potionEffect;
	}
}
