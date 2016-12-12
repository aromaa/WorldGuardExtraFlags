package net.goldtreeservers.worldguardextraflags.flags;

import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StringFlag;

public class CaseForcedStringFlag extends StringFlag
{
	private final boolean upperCase;
	
	public CaseForcedStringFlag(String name, boolean upperCase)
	{
		super(name);
		
		this.upperCase = upperCase;
	}
	
    @Override
    public String parseInput(FlagContext context) throws InvalidFlagFormat
    {
        return this.upperCase ? super.parseInput(context).toUpperCase() : super.parseInput(context).toLowerCase();
    }
}
