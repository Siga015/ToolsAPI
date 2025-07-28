package siga.toolsapi.task;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class TaskBase extends BukkitRunnable {

    private final JavaPlugin plugin;
    private boolean isCancelled;

    private Runnable finishAction;

    public TaskBase(JavaPlugin plugin) {
        this.plugin = plugin;
        this.isCancelled = false;
    }


    protected abstract int setTicks();

    public void start() {
        runTaskTimer(plugin, 0, setTicks());
    }


    public void stop() {
        cancel();
        isCancelled = true;
        if (finishAction != null) finishAction.run();
    }

    public void setOnFinish(Runnable action) {
        this.finishAction = action;
    }



    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
}
