package siga.toolsapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.PlayerData;
import siga.toolsapi.util.ColorTranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class GUI implements Listener {

    private final JavaPlugin plugin;
    private final GUIShape shape;
    private Inventory gui;
    private final List<GuiButton> buttons = new ArrayList<>();

    private static boolean registered = false;

    public GUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.shape = setShape();
        this.gui = Bukkit.createInventory(null, shape.getRowsLength() * 9, ColorTranslator.translate(setName()));

        applyShape(setShape());
        register();

    }


    public void show(Player player) {
        refresh(player);

        player.openInventory(gui);
        PlayerData.getPlayer(player).setCurrentGUI(this);
    }

    protected abstract String setName();
    protected abstract GUIShape setShape();


    public void setItem(int index, ItemStack itemStack) {
        gui.setItem(index, itemStack);
    }

    public void setButton(int index, GuiButton button) {
        buttons.add(button);
        gui.setItem(index, button.getItem());
    }

    public void addItem(ItemStack item) {
        for (int i = 0; i < this.gui.getSize(); i++) {
            if (this.gui.getItem(i) == null || Objects.requireNonNull(this.gui.getItem(i)).getType() == Material.AIR) {;
                this.gui.setItem(i, item);
                return;
            }
        }
    }


    public void addButton(GuiButton button) {
        for (int i = 0; i < this.gui.getSize(); i++) {
            if (this.gui.getItem(i) == null || Objects.requireNonNull(this.gui.getItem(i)).getType() == Material.AIR) {
                this.buttons.add(button);
                this.gui.setItem(i, button.getItem());
                return;
            }
        }
    }

    public void applyShape(GUIShape shape) {
        this.gui = Bukkit.createInventory(null, shape.getRowsLength() * 9, ColorTranslator.translate(setName()));
        this.buttons.clear();

        int slot = 0;
        for (String row : shape.getRows()) {
            for (int i = slot; i < row.length() + slot; i++) {
                char c = row.charAt(i - slot);

                if (shape.getItems().get(c) != null) {
                    ItemStack item = shape.getItems().get(c);
                    gui.setItem(i, item);
                }

                if (shape.getButtons().get(c) != null) {
                    GuiButton button = shape.getButtons().get(c);
                    buttons.add(button);
                    gui.setItem(i, button.getItem());
                }
            }
            slot += 9;
        }
    }

    public void refresh(Player player) {
        for (int i = 0; i < gui.getSize(); i++) {
            ItemStack item = gui.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;

            for (GuiButton button : buttons) {
                if (areItemsSimilar(button.getItem(), item)) {
                    button.updateLore(player);
                    gui.setItem(i, button.getItem());
                    break;
                }
            }
        }
    }



    private boolean handleButton(ItemStack item, Player player) {
        GuiButton button = getButtonByItem(item);

        if (button != null) {
            if (button.getCloseInventory()) player.closeInventory();
            if (button.getAction() != null) button.getAction().execute(player);
            return true;
        }
        return false;
    }


    private boolean handleShiftButton(ItemStack item, Player player) {
        GuiButton button = getButtonByItem(item);
        if (button != null) {
            if (button.getShiftAction() != null) {
                button.getShiftAction().execute(player);
            }
            return true;
        }
        return false;
    }

    private GuiButton getButtonByItem(ItemStack item) {
        return buttons.stream()
                .filter(button -> areItemsSimilar(button.getItem(), item))
                .findAny()
                .orElse(null);
    }




    private void register() {
        if (!registered) {
            registered = true;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (PlayerData.getPlayer(player).getCurrentGUI() != null) {
            PlayerData.getPlayer(player).setCurrentGUI(null);
        }
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;


        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        PlayerData data = PlayerData.getPlayer(player);

        if (data != null && data.getCurrentGUI() != null && item != null) {
            GUI gui = data.getCurrentGUI();
            if (event.isShiftClick()) {
                if (gui.handleShiftButton(item, player)) {
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
            else if (gui.handleButton(item, player)) event.setCancelled(true);
        }
    }

    private boolean areItemsSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) return false;

        if (item1.getType() != item2.getType()) return false;


        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            if (item1.getItemMeta().hasDisplayName() && item2.getItemMeta().hasDisplayName()) {
                if (!item1.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName())) {
                    return false;
                }
            } else if (item1.getItemMeta().hasDisplayName() || item2.getItemMeta().hasDisplayName()) {
                return false;
            }

            if (item1.getItemMeta().hasLore() && item2.getItemMeta().hasLore()) {
                if (!item1.getItemMeta().getLore().equals(item2.getItemMeta().getLore())) {
                    return false;
                }
            } else if (item1.getItemMeta().hasLore() || item2.getItemMeta().hasLore()) {
                return false;
            }
        } else if (item1.hasItemMeta() || item2.hasItemMeta()) {
            return false;
        }

        if (!item1.getEnchantments().equals(item2.getEnchantments())) {
            return false;
        }

        return true;
    }


}
