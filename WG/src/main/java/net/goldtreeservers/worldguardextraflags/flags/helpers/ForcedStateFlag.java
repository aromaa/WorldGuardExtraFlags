package net.goldtreeservers.worldguardextraflags.flags.helpers;

import java.util.Collection;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

import net.goldtreeservers.worldguardextraflags.flags.helpers.ForcedStateFlag.ForcedState;

public class ForcedStateFlag extends Flag<ForcedState>
{
	public static enum ForcedState
	{
		ALLOW,
		DENY,
		FORCE
	}
	
	public ForcedStateFlag(String name)
	{
		super(name);
	}

    @Override
    public ForcedState getDefault()
    {
        return ForcedState.ALLOW;
    }
    
    @Override
    public boolean hasConflictStrategy()
    {
        return true;
    }
    
    @Override
    public ForcedState chooseValue(Collection<ForcedState> values)
    {
    	//Order is the following: Deny > Force > Allow
    	ForcedState result = null;
    	
        if (!values.isEmpty())
        {
        	for (ForcedState state : values)
        	{
        		if (state == ForcedState.DENY)
        		{
        			return ForcedState.DENY;
        		}
        		else if (state == ForcedState.FORCE)
        		{
        			result = ForcedState.FORCE;
        		}
        		else if (state == ForcedState.ALLOW)
        		{
        			if (result == null)
        			{
        				result = ForcedState.ALLOW;
        			}
        		}
        	}
        }
        
    	return result;
    }


	@Override
	public ForcedState parseInput(FlagContext context) throws InvalidFlagFormat
	{
        String input = context.getUserInput();

        if (input.equalsIgnoreCase("allow"))
        {
            return ForcedState.ALLOW;
        }
        else if (input.equalsIgnoreCase("force"))
        {
            return ForcedState.FORCE;
        }
        else if (input.equalsIgnoreCase("deny"))
        {
            return ForcedState.DENY;
        }
        else if (input.equalsIgnoreCase("none"))
        {
            return null;
        }
        else
        {
            throw new InvalidFlagFormat("Expected none/allow/force/deny but got '" + input + "'");
        }
	}

	@Override
	public ForcedState unmarshal(Object o)
	{
        String str = o.toString();
        
        switch(str)
        {
	        case "ALLOW":
	        	return ForcedState.ALLOW;
	        case "FORCE":
	        	return ForcedState.FORCE;
	        case "DENY":
	        	return ForcedState.DENY;
        	default:
        		return null;
        }
	}

	@Override
	public Object marshal(ForcedState o)
	{
		if (o == null)
		{
			return null;
		}
		
		return o.toString();
	}
}
