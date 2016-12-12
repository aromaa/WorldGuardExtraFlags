package net.goldtreeservers.worldguardextraflags.flags;

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
		return o.getName();
	}

	@Override
	public PotionEffectType parseInput(FlagContext context) throws InvalidFlagFormat
	{
        return PotionEffectType.getByName(context.getUserInput().trim());
	}

	@Override
	public PotionEffectType unmarshal(Object o)
	{
		return PotionEffectType.getByName(o.toString());
	}
}
