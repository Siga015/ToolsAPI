package siga.toolsapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.PlayerData;
import siga.toolsapi.util.ColorTranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class GUI {

    private final JavaPlugin plugin;
    private GUIShape shape;
    private Inventory gui;
    private final List<GuiButton> buttons = new ArrayList<>();
    private String name;
    private GuiFilter filter;

    private UUID uuid;

    public GUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.shape = setShape();
        this.gui = Bukkit.createInventory(null, shape.getRowsLength() * 9, ColorTranslator.translate(setName()));
        this.uuid = UUID.randomUUID();
        this.name = ColorTranslator.translate(setName());
        this.filter = setGuiFilter();

        applyShape(setShape());
    }


    public void show(Player player) {
        refresh(player);
        player.openInventory(gui);
        PlayerData.getPlayer(player).setCurrentGUI(this);
    }


    protected abstract String setName();
    protected abstract GUIShape setShape();
    protected abstract GuiFilter setGuiFilter();


    public void setItem(int index, ItemStack itemStack) {
        gui.setItem(index, itemStack.clone());
    }

    public void setButton(int index, GuiButton button) {
        buttons.add(button);
        gui.setItem(index, button.getItem().clone());
    }

    public void addItem(ItemStack item) {
        for (int i = 0; i < this.gui.getSize(); i++) {
            if (this.gui.getItem(i) == null || Objects.requireNonNull(this.gui.getItem(i)).getType() == Material.AIR) {;
                this.gui.setItem(i, item.clone());
                return;
            }
        }
    }


    public void addButton(GuiButton button) {
        for (int i = 0; i < this.gui.getSize(); i++) {
            if (this.gui.getItem(i) == null || Objects.requireNonNull(this.gui.getItem(i)).getType() == Material.AIR) {
                this.buttons.add(button);
                this.gui.setItem(i, button.getItem().clone());
                return;
            }
        }
    }

    public void applyShape(GUIShape shape) {
        this.shape = shape;
        this.gui = Bukkit.createInventory(null, shape.getRowsLength() * 9, ColorTranslator.translate(name));
        this.buttons.clear();

        int slot = 0;
        for (String row : shape.getRows()) {
            for (int i = slot; i < row.length() + slot; i++) {
                char c = row.charAt(i - slot);

                if (shape.getItems().get(c) != null) {
                    ItemStack item = shape.getItems().get(c);
                    gui.setItem(i, item.clone());
                }

                if (shape.getButtons().get(c) != null) {
                    GuiButton button = shape.getButtons().get(c);
                    buttons.add(button);
                    gui.setItem(i, button.getItem().clone());
                }
            }
            slot += 9;
        }
    }

    public void applyNewName(String name) {
        this.name = ColorTranslator.translate(name);
        applyShape(this.shape);
    }

    public void applyFilter(GuiFilter filter) {
        this.filter = filter;
    }

    public void removeButton(GuiButton guiButton, Player player) {
        int amount = guiButton.getItem().getAmount();

        if (amount == 1) {
            buttons.remove(guiButton);
            gui.remove(guiButton.getItem());
        }
        else {
            guiButton.getItem().setAmount(guiButton.getItem().getAmount() -1);
        }



        refresh(player);
    }

    public void refresh(Player player) {
        for (int i = 0; i < gui.getSize(); i++) {
            ItemStack item = gui.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;

            for (GuiButton button : buttons) {
                if (button.getItem().isSimilar(item)) {
                    button.updateLore(player);
                    gui.setItem(i, button.getItem().clone());
                    break;
                }
            }
        }
    }


    public Inventory getInventory() {
        return gui;
    }

    public GuiFilter getFilter() {
        return filter;
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


    private boolean handleShiftButton(ItemStack item, Player player, ShiftClick click) {
        GuiButton button = getButtonByItem(item);
        if (button != null) {

            if (button.getShiftAction(click) != null) {
                button.getShiftAction(click).execute(player);
            }
            return true;
        }
        return false;
    }



    private GuiButton getButtonByItem(ItemStack item) {
        return buttons.stream()
                .filter(button -> button.getItem().isSimilar(item))
                .findAny()
                .orElse(null);
    }


    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        // ReadOnly GUI
        if (this instanceof ReadOnly) {
            if (event.getClick() == ClickType.NUMBER_KEY) {
                event.setCancelled(true);
                return;
            }

            if (event.getClick().isKeyboardClick()) {
                event.setCancelled(true);
                return;
            }

            if (event.getClickedInventory() != null &&
                    event.getClickedInventory().equals(player.getInventory())) {
                event.setCancelled(true);
                return;
            }
        }

        ItemStack movedItem = getMovedItem(event);


        if (filter != null) {
            if (event.getRawSlot() < gui.getSize()) {

                if (movedItem != null && movedItem.getType() != Material.AIR) {

                    if (!filter.filter(movedItem)) {
                        event.setCancelled(true);
                    }
                }
            }
        }

        if (event.isShiftClick()) {
            if (event.isRightClick()) {
                if (handleShiftButton(item, player, ShiftClick.RIGHT)) {
                    event.setCancelled(true);
                }
            } else if (event.isLeftClick()) {
                if (handleShiftButton(item, player, ShiftClick.LEFT)) {
                    event.setCancelled(true);
                }
            }
        } else {
            if (handleButton(item, player)) {
                event.setCancelled(true);
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, player::updateInventory, 1);
    }

    public void handleDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (this instanceof ReadOnly) {
            for (int slot : event.getRawSlots()) {
                if (slot < gui.getSize()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    public void handleClose(Player player) {
        PlayerData.getPlayer(player).setCurrentGUI(null);
    }


//    @EventHandler
//    public void onClose(InventoryCloseEvent event) {
//        Player player = (Player) event.getPlayer();
//
//        if (PlayerData.getPlayer(player).getCurrentGUI() != null) {
//            PlayerData.getPlayer(player).setCurrentGUI(null);
//        }
//    }



//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onClick(InventoryClickEvent event) {
//        if (!(event.getWhoClicked() instanceof Player)) return;
//
//
//        Player player = (Player) event.getWhoClicked();
//        ItemStack item = event.getCurrentItem();
//        PlayerData data = PlayerData.getPlayer(player);
//
//        if (data != null && data.getCurrentGUI() != null) {
//            GUI gui = data.getCurrentGUI();
//
//            if (!event.getView().getTopInventory().equals(gui.getInventory())) return;
//
//            // Read only GUIs
//            if (gui instanceof ReadOnly) {
//                if (event.getClick() == ClickType.NUMBER_KEY) {
//                    event.setCancelled(true);
//                    return;
//                }
//
//                if (event.getClick().isKeyboardClick()) {
//                    event.setCancelled(true);
//                    return;
//                }
//
//                if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getInventory())) {
//                    event.setCancelled(true);
//                    return;
//                }
//            }
//
//            ItemStack movedItem = getMovedItem(event);
//
//            if (gui.getFilter() != null) {
//
//                if (event.getRawSlot() < gui.getInventory().getSize()) {
//
//                    if (movedItem == null || movedItem.getType() == Material.AIR) {
//                        event.setCancelled(true);
//                        return;
//                    }
//
//                    if (!gui.getFilter().filter(movedItem)) {
//                        event.setCancelled(true);
//                        return;
//                    }
//                }
//            }
//
//            if (event.isShiftClick()) {
//
//                if (event.isRightClick()) {
//                    if (gui.handleShiftButton(item, player, ShiftClick.RIGHT)) {
//                        event.setCancelled(true);
//                        player.updateInventory();
//                    }
//                }
//                else if (event.isLeftClick()){
//                    if (gui.handleShiftButton(item, player, ShiftClick.LEFT)) {
//                        event.setCancelled(true);
//                        player.updateInventory();
//                    }
//                }
//            }
//            else if (gui.handleButton(item, player)) event.setCancelled(true);
//
//            Bukkit.getScheduler().runTaskLater(plugin, player::updateInventory,1);
//        }
//    }

//    @EventHandler
//    public void onDrag(InventoryDragEvent event) {
//        if (!(event.getWhoClicked() instanceof Player)) return;
//
//        Player player = (Player) event.getWhoClicked();
//
//        PlayerData data = PlayerData.getPlayer(player);
//        if (data == null || data.getCurrentGUI() == null) return;
//
//        GUI gui = data.getCurrentGUI();
//
//        if (gui instanceof ReadOnly) {
//            for (int slot : event.getRawSlots()) {
////                if (slot < gui.getInventory().getSize()) {
//                    event.setCancelled(true);
//                    return;
////                }
//            }
//        }
//    }

    private ItemStack getMovedItem(InventoryClickEvent event) {

        if (event.getClick() == ClickType.NUMBER_KEY) {
            int hotbar = event.getHotbarButton();
            return event.getWhoClicked().getInventory().getItem(hotbar);
        }

        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
            return event.getCursor();
        }

        return event.getCurrentItem();
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


    public UUID getUuid() {
        return uuid;
    }
}
