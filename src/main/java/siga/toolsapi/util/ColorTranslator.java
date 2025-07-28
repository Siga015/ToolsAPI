package siga.toolsapi.util;

import org.bukkit.ChatColor;

public class ColorTranslator {

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }


}
