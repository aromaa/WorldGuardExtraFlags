package net.goldtreeservers.worldguardextraflags.utils;

import com.boydti.fawe.FaweAPI;

public class PluginUtils
{
	public static void registerFAWE()
	{
		FaweAPI.addMaskManager(new FaweWorldEditFlag());
	}
}
