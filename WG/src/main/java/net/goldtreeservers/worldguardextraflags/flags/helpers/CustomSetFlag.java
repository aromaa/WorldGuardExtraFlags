package net.goldtreeservers.worldguardextraflags.flags.helpers;

import java.util.Set;

import com.google.common.collect.Sets;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.SetFlag;

public class CustomSetFlag<T> extends SetFlag<T>
{
	public CustomSetFlag(String name, Flag<T> subFlag)
	{
		super(name, subFlag);
	}
	
	@Override
    public Set<T> parseInput(FlagContext context) throws InvalidFlagFormat
	{
        String input = context.getUserInput();
        if (input.isEmpty())
        {
            return Sets.newHashSet();
        }
        else
        {
            Set<T> items = Sets.newLinkedHashSet();

            for (String str : input.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1))
            {
            	if (str.startsWith("\"") && str.endsWith("\""))
            	{
            		str = str.substring(1, str.length() - 1);
            	}

                FlagContext copy = context.copyWith(null, str, null);
                items.add(this.getType().parseInput(copy));
            }

            return items;
        }
    }

    @Override
    public Set<T> unmarshal(Object o)
    {
        if (!(o instanceof Iterable<?> iterable))
        {
            return null;
        }

        Set<T> items = Sets.newLinkedHashSet();

        iterable.forEach(i ->
        {
            final T value = this.getType().unmarshal(i);
            if (value != null)
            {
                items.add(value);
            }
        });

        return items;
    }
}
