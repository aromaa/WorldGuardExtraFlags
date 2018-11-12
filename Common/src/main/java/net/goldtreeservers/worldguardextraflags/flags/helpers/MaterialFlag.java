package net.goldtreeservers.worldguardextraflags.flags.helpers;

import org.bukkit.Material;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class MaterialFlag extends Flag<Material>
{
	public MaterialFlag(String name)
	{
		super(name);
	}

	@Override
	public Object marshal(Material o)
	{
		return o.toString();
	}

	@Override
	public Material parseInput(FlagContext context) throws InvalidFlagFormat
	{
		Material material = Material.getMaterial(context.getUserInput().trim().toUpperCase());
		if (material != null)
		{
			return material;
		}
		else
		{
			throw new InvalidFlagFormat("Unable to find the material!");
		}
	}

	@Override
	public Material unmarshal(Object o)
	{
		return Material.getMaterial(o.toString());
	}
}
