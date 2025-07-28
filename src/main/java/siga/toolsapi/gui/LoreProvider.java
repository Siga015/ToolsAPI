package siga.toolsapi.gui;

import org.bukkit.entity.Player;

import java.util.List;

public interface LoreProvider {

    List<String> getLore(Player player);

}
