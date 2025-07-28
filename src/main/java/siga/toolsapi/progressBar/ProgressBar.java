package siga.toolsapi.progressBar;

import com.google.common.base.Strings;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import siga.toolsapi.util.ColorTranslator;

import java.util.HashMap;

public class ProgressBar {

    private static final HashMap<Player, Timer> playerTimer = new HashMap<>();
    private final String previousMessage;

    private int currentProgress;
    private final int maxProgress;

    private final int bars;

    private final ChatColor notCompletedColor;
    private final ChatColor completedColor;
    private final char symbol = '|';

    private BarAction resultAction;
    private BarAction progressAction;
    private StopCondition stopCondition;
    private BarAction breakAction;


    public ProgressBar(String previousMessage, int currentProgress, int maxProgress, int bars, ChatColor notCompletedColor, ChatColor completedColor) {
        this.previousMessage = ColorTranslator.translate(previousMessage);
        this.currentProgress = currentProgress;
        this.maxProgress = maxProgress;
        this.bars = bars;
        this.notCompletedColor = notCompletedColor;
        this.completedColor = completedColor;
    }


    public void start(Player player, JavaPlugin plugin) {
        Timer timer = new Timer(player, this);
        if (!playerTimer.containsKey(player)) {
            timer.start(plugin);
            playerTimer.put(player, timer);
        }
    }


    public void stop(Player player) {
        if (playerTimer.containsKey(player)) {
            Timer timer = playerTimer.get(player);
            timer.stop();
        }
    }

    public void setResultAction(BarAction action) {
        this.resultAction = action;
    }

    public void setStopCondition(StopCondition stopCondition) {
        this.stopCondition = stopCondition;
    }

    public void setProgressAction(BarAction progressAction) {
        this.progressAction = progressAction;
    }

    public void setBreakAction(BarAction breakAction) {
        this.breakAction = breakAction;
    }


    public static class Timer extends BukkitRunnable {

        private final Player player;
        private final String previousMessage;

        private final int max;
        private final int totalBars;
        private final ChatColor notCompletedColor;
        private final ChatColor completedColor;

        private final char symbol;
        private final BarAction action;
        private final StopCondition stopCondition;
        private final BarAction progressAction;
        private final BarAction breakAction;

        private final ProgressBar bar;


        public Timer(Player player, ProgressBar bar) {
            this.player = player;

            this.previousMessage = bar.previousMessage;
            this.max = bar.maxProgress;
            this.totalBars = bar.bars;
            this.notCompletedColor = bar.notCompletedColor;
            this.completedColor = bar.completedColor;

            this.symbol = bar.symbol;
            this.action = bar.resultAction;
            this.stopCondition = bar.stopCondition;
            this.progressAction = bar.progressAction;
            this.breakAction = bar.breakAction;

            this.bar = bar;
        }


        @Override
        public void run() {
            if (!player.isOnline() || player.isDead() || stopCondition != null && stopCondition.shouldStop()) {
                stop();

                return;
            }

            if (progressAction != null) progressAction.execute(player);

            float percent = (float) bar.getCurrentProgress() / max;
            int progressBars = (int) (totalBars * percent);

            String displayProgress = ColorTranslator.translate(previousMessage + "&r[" +
                    Strings.repeat("" + completedColor + symbol, progressBars) +
                    Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars) + "&r]");

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(displayProgress));

            if (bar.getCurrentProgress() == max) {
                stop();
                if (action != null) action.execute(player);
            }

            bar.setCurrentProgress(bar.getCurrentProgress() + 1);
        }


        public void start(JavaPlugin plugin) {
            runTaskTimer(plugin, 0, 0);
        }

        public void stop() {
            cancel();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            if (breakAction != null) breakAction.execute(player);
            playerTimer.remove(player);
        }
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    private void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }
}
