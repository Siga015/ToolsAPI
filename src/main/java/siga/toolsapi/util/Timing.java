package siga.toolsapi.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Timing {

    public static void callDelay(JavaPlugin plugin, float seconds, Runnable action) {
        long ticks = (long) (seconds * 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                action.run();
                this.cancel();
            }
        }.runTaskLater(plugin, ticks);
    }

    public static void callDelay(JavaPlugin plugin, long tick, Runnable action) {

        new BukkitRunnable() {
            @Override
            public void run() {
                action.run();
                this.cancel();
            }
        }.runTaskLater(plugin, tick);
    }
}
