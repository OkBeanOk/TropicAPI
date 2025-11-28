package com.okbeanok.tropicapi.api.util.effects;

import com.okbeanok.tropicapi.api.util.reflection.ReflectionUtils;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;

/*
 * References
 *
 * * * GitHub: https://github.com/CryptoMorin/XSeries/blob/master/Titles.java
 * * XSeries: https://www.spigotmc.org/threads/378136/
 * PacketPlayOutTitle: https://wiki.vg/Protocol#Title
 *
 */

/**
 * A reflection API for titles in Minecraft.
 * Fully optimized - Supports 1.8.8+ and above.
 * Requires ReflectionUtils.
 * Messages are not colorized by default.
 * <p>
 * Titles are text messages that appear in the
 * middle of the players screen: https://minecraft.gamepedia.com/Commands/title
 * PacketPlayOutTitle: https://wiki.vg/Protocol#Title
 *
 * @author Crypto Morin
 * @version 1.0.0
 * @see ReflectionUtils
 */

public class Titles {

    private static final Object TIMES;
    private static final Object TITLE;
    private static final Object SUBTITLE;
    private static final Object CLEAR;

    private static final MethodHandle PACKET;
    private static final MethodHandle CHAT_COMPONENT_TEXT;

    static {
        Class<?> chatComponentText = ReflectionUtils.getNMSClass("ChatComponentText");
        Class<?> packet = ReflectionUtils.getNMSClass("PacketPlayOutTitle");
        Class<?> titleTypes = packet.getDeclaredClasses()[0];
        MethodHandle packetCtor = null;
        MethodHandle chatComp = null;

        Object times = null;
        Object title = null;
        Object subtitle = null;
        Object clear = null;

        for (Object type : titleTypes.getEnumConstants()) {
            switch (type.toString()) {
                case "TIMES":
                    times = type;
                    break;
                case "TITLE":
                    title = type;
                    break;
                case "SUBTITLE":
                    subtitle = type;
                    break;
                case "CLEAR":
                    clear = type;
            }
        }

        try {
            chatComp = MethodHandles.lookup().findConstructor(chatComponentText,
                    MethodType.methodType(void.class, String.class));

            packetCtor = MethodHandles.lookup().findConstructor(packet,
                    MethodType.methodType(void.class, titleTypes,
                            ReflectionUtils.getNMSClass("IChatBaseComponent"),
                            int.class, int.class, int.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }

        TITLE = title;
        SUBTITLE = subtitle;
        TIMES = times;
        CLEAR = clear;

        PACKET = packetCtor;
        CHAT_COMPONENT_TEXT = chatComp;
    }

    public static void sendTitle(Player player,
                                 int fadeIn, int stay, int fadeOut,
                                 String title, String subtitle) {
        Objects.requireNonNull(player, "Cannot send title to null player");
        if (title == null && subtitle == null) return;

        try {
            Object timesPacket = PACKET.invoke(TIMES, CHAT_COMPONENT_TEXT.invoke(title), fadeIn, stay, fadeOut);
            ReflectionUtils.sendPacket(player, timesPacket);

            if (title != null) {
                Object titlePacket = PACKET.invoke(TITLE, CHAT_COMPONENT_TEXT.invoke(title), fadeIn, stay, fadeOut);
                ReflectionUtils.sendPacket(player, titlePacket);
            }
            if (subtitle != null) {
                Object subtitlePacket = PACKET.invoke(SUBTITLE, CHAT_COMPONENT_TEXT.invoke(subtitle), fadeIn, stay, fadeOut);
                ReflectionUtils.sendPacket(player, subtitlePacket);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void clearTitle(Player player) {
        Objects.requireNonNull(player, "Cannot clear title from null player");
        Object clearPacket = null;

        try {
            clearPacket = PACKET.invoke(CLEAR, null, -1, -1, -1);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        ReflectionUtils.sendPacket(player, clearPacket);
    }

}
