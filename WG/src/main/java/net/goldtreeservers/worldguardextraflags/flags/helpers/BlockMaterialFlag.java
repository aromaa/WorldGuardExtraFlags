package net.goldtreeservers.worldguardextraflags.flags.helpers;

import org.bukkit.Material;

import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class BlockMaterialFlag extends MaterialFlag
{
	public BlockMaterialFlag(String name)
	{
		super(name);
	}

	@Override
	public Material parseInput(FlagContext context) throws InvalidFlagFormat
	{
		Material material = super.parseInput(context);
		if (!material.isBlock())
		{
			throw new InvalidFlagFormat("This material isn't seen as 'placable block', use alternative id");
		}
		
		return material;
	}
}
