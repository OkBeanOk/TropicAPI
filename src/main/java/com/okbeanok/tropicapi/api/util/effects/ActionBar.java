package com.okbeanok.tropicapi.api.util.effects;

import com.okbeanok.tropicapi.TropicAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Modern ActionBar helper using Adventure API.
 * Works on Paper/Leaf 1.17+ without NMS.
 *
 * @author Crypto Morin (original idea)
 * @version 2.0.0
 */
public class ActionBar {

    private static final JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(TropicAPI.class);

    /**
     * Sends an action bar to a player.
     *
     * @param player  the player to send the action bar to.
     * @param message the message to send.
     */
    public static void sendActionBar(Player player, String message) {
        Objects.requireNonNull(player, "Cannot send action bar to null player");
        if (message == null) message = "";
        player.sendActionBar(Component.text(message));
    }

    /**
     * Sends an action bar to all online players.
     *
     * @param message the message to send.
     */
    public static void sendAllActionBar(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendActionBar(player, message);
        }
    }

    /**
     * Sends an action bar to a player while a condition is true.
     * Message is constant.
     *
     * @param player   target player
     * @param message  message to send (not updated)
     * @param callable condition; if returns false, task stops
     */
    public static void sendActionBarWhile(Player player,
                                          String message,
                                          Callable<Boolean> callable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!callable.call()) {
                        cancel();
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    cancel();
                    return;
                }
                sendActionBar(player, message);
            }
        }.runTaskTimerAsynchronously(PLUGIN, 0L, 40L); // every 2 seconds
    }

    /**
     * Sends an action bar to a player while a condition is true.
     * Message is recomputed for each send.
     *
     * @param player   target player
     * @param message  supplies the message for each tick
     * @param callable condition; if returns false, task stops
     */
    public static void sendActionBarWhile(Player player,
                                          Callable<String> message,
                                          Callable<Boolean> callable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!callable.call()) {
                        cancel();
                        return;
                    }
                    sendActionBar(player, message.call());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(PLUGIN, 0L, 40L); // every 2 seconds
    }

    /**
     * Sends an action bar to a player for a specific duration (in ticks).
     *
     * @param player   target player
     * @param message  message to send
     * @param duration duration in ticks to keep re-sending
     */
    public static void sendActionBar(Player player, String message, long duration) {
        if (duration < 1) return;

        new BukkitRunnable() {
            long remaining = duration;

            @Override
            public void run() {
                if (remaining <= 0L) {
                    cancel();
                    return;
                }
                sendActionBar(player, message);
                remaining -= 40L; // 2 seconds per run (40 ticks)
            }
        }.runTaskTimerAsynchronously(PLUGIN, 0L, 40L);
    }
}
