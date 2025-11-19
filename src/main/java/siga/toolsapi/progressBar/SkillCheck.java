package siga.toolsapi.progressBar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SkillCheck implements Listener {

    private static final Map<UUID, SkillCheckInstance> activeChecks = new HashMap<>();
    private final JavaPlugin plugin;

    public SkillCheck(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    public void startSkillCheck(Player player, int bars, int speed, int minGreenBars, Runnable successAction, Runnable failAction) {
        if (activeChecks.containsKey(player.getUniqueId())) return;

        int markerPos = 0;
        int greenStart = new Random().nextInt(bars - 4);
        int greenLength = minGreenBars + new Random().nextInt(1);

        SkillCheckInstance instance = new SkillCheckInstance(player, bars, markerPos, greenStart, greenLength, speed, successAction, failAction);
        activeChecks.put(player.getUniqueId(), instance);
        instance.runTaskTimer(plugin, 0, speed);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        SkillCheckInstance instance = activeChecks.get(player.getUniqueId());
        if (instance == null) return;

        if (!event.isSneaking()) return;

        instance.checkSuccess();
    }


    public static boolean hasSkillCheck(Player player) {
        return activeChecks.containsKey(player.getUniqueId());
    }

    public static void cancelSkillCheck(Player player) {
        if (!hasSkillCheck(player)) return;

        SkillCheckInstance skill = activeChecks.get(player.getUniqueId());
        skill.end(false);
    }

    private static class SkillCheckInstance extends BukkitRunnable {

        private final Player player;
        private final int bars;
        private int markerPos;
        private final int greenStart;
        private final int greenEnd;
        private final Runnable successAction;
        private final Runnable failAction;

        private boolean finished = false;

        public SkillCheckInstance(Player player, int bars, int markerPos, int greenStart, int greenLength, int speed, Runnable successAction, Runnable failAction) {
            this.player = player;
            this.bars = bars;
            this.markerPos = markerPos;
            this.greenStart = greenStart;
            this.greenEnd = greenStart + greenLength;
            this.successAction = successAction;
            this.failAction = failAction;
        }

        @Override
        public void run() {
            if (!player.isOnline() || player.isDead()) {
                end(false);
                return;
            }

            if (finished) {
                cancel();
                return;
            }

            sendBar();
            markerPos++;

            if (markerPos > bars) {
                end(false);
            }
        }

        private void sendBar() {
            StringBuilder sb = new StringBuilder(ChatColor.GRAY + "[");
            for (int i = 0; i < bars; i++) {
                if (i == markerPos) {
                    sb.append(ChatColor.WHITE).append("|");
                } else if (i >= greenStart && i < greenEnd) {
                    sb.append(ChatColor.GREEN).append("-");
                } else {
                    sb.append(ChatColor.DARK_GRAY).append("-");
                }
            }
            sb.append(ChatColor.GRAY).append("]");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(sb.toString()));
            player.sendTitle("", "§l§6SHIFT", 0, 20, 0);
        }

        public void checkSuccess() {
            if (finished) return;

            if (markerPos >= greenStart && markerPos < greenEnd) {
                end(true);
            } else {
                end(false);
            }
        }

        private void end(boolean success) {
            finished = true;
            cancel();
            activeChecks.remove(player.getUniqueId());

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            player.sendTitle("", "", 0, 5, 0);



            if (success && successAction != null) {
                successAction.run();
            } else if (!success && failAction != null) {
                failAction.run();
            }
        }
    }
}
