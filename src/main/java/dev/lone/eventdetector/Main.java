package dev.lone.eventdetector;

import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin implements Listener
{
    public static Main inst;

    List<String> hookedListeners = new ArrayList<>();

    @Override
    public void onEnable()
    {
        inst = this;
        Settings.load(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        findRegisteredHandlers();
    }

    @EventHandler
    private void onPluginEnable(PluginEnableEvent e)
    {
        findRegisteredHandlers();
    }

    private void findRegisteredHandlers()
    {
        boolean any = false;
        for (HandlerList handler : HandlerList.getHandlerLists())
        {
            for (RegisteredListener otherPluginListener : handler.getRegisteredListeners())
            {
                Class<?> clazz = otherPluginListener.getListener().getClass();
                for (Method method : clazz.getDeclaredMethods())
                {
                    if(method.getParameterCount() != 1)
                        continue;
                    EventHandler annotation = null;
                    try
                    {
                        annotation = method.getAnnotation(EventHandler.class);
                    }
                    catch (NullPointerException ignored) {}

                    if (annotation == null)
                        continue;

                    Class<?> maybeEventClazz = method.getParameterTypes()[0];
                    if (!Event.class.isAssignableFrom(maybeEventClazz))
                        continue;

                    @SuppressWarnings({"unchecked"})
                    Class<Event> eventType = (Class<Event>) maybeEventClazz;
                    if(Settings.isRegistered(eventType))
                    {
                        String key;
                        if (Settings.PRINT_DETAILED)
                            key = otherPluginListener.getListener().getClass().getName();
                        else
                            key = eventType.getName() + "_" + otherPluginListener.getPlugin().getName();
                        if(hookedListeners.contains(key))
                            continue;

                        hookedListeners.add(key);
                        any = true;

                        Main.inst.getLogger().info(eventType.getName() + " registered by " + otherPluginListener.getPlugin().getName());
                        if(Settings.PRINT_DETAILED)
                            Main.inst.getLogger().info(otherPluginListener.getListener().getClass().getName() + "#" + method.getName() + "(...) | " + annotation.priority() + " ignoreCancelled: " + annotation.ignoreCancelled());
                    }
                }
            }
        }

        if(any)
            getLogger().info(" ");
    }
}
