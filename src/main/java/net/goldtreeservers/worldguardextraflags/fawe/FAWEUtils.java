package net.goldtreeservers.worldguardextraflags.fawe;

import com.boydti.fawe.FaweAPI;

public class FAWEUtils
{
	public static void registerFAWE()
	{
		FaweAPI.addMaskManager(new FaweWorldEditFlagMaskManager());
	}
}
