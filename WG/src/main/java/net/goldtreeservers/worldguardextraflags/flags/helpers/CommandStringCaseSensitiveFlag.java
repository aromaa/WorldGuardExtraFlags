package net.goldtreeservers.worldguardextraflags.flags.helpers;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;

public class CommandStringCaseSensitiveFlag extends Flag<String>
{
    public CommandStringCaseSensitiveFlag(String name)
    {
        super(name);
    }

    public Object marshal(String o)
    {
        return o;
    }

    public String parseInput(FlagContext context)
    {
        String input = context.getUserInput().trim();
        if (!input.startsWith("/"))
        {
            input = "/" + input;
        }

        return input;
    }

    public String unmarshal(Object o)
    {
        return o instanceof String string ? string : null;
    }
}
