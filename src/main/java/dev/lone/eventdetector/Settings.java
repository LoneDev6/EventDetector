package dev.lone.eventdetector;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Settings
{
    public static boolean PRINT_DETAILED;
    public static List<String> EVENTS_TO_CATCH;

    public static void load(Plugin plugin)
    {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        PRINT_DETAILED = config.getBoolean("print_detailed");
        EVENTS_TO_CATCH = config.getStringList("events_to_catch");
    }

    public static boolean isRegistered(Class<Event> event)
    {
        return EVENTS_TO_CATCH.contains(event.getName()) || EVENTS_TO_CATCH.contains(event.getSimpleName());
    }
}
