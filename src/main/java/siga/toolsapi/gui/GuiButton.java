package siga.toolsapi.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import siga.toolsapi.util.ColorTranslator;

import java.util.Arrays;
import java.util.List;

public class GuiButton {

    private final ItemStack item;
    private final String title;
    private ClickAction action;
    private boolean closeInventory;


    public GuiButton(String title, Material material) {
        this.title = title;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorTranslator.translate(title));
        meta.setLocalizedName("gui_button");

        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }

        item.setItemMeta(meta);
        this.item = item;
    }

    public GuiButton(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            throw new IllegalStateException("ItemStack cannot be null!");
        }

        this.title = item.getItemMeta().getDisplayName();
        ItemMeta meta = item.getItemMeta();
        meta.setLocalizedName("gui_button");
        item.setItemMeta(meta);
        this.item = item;
    }


    public void setLore(String... lore) {
        ItemMeta meta = item.getItemMeta();

        List<String> loreList = Arrays.asList(lore);
        loreList.replaceAll(ColorTranslator::translate);

        meta.setLore(loreList);
        item.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return item;
    }

    public ClickAction getAction() {
        return action;
    }

    public boolean getCloseInventory() {
        return closeInventory;
    }

    public String getTitle() {
        return title;
    }

    public GuiButton onClick(ClickAction action) {
        this.action = action;
        return this;
    }

    public GuiButton withLore(String... lore) {
        setLore(lore);
        return this;
    }

    public GuiButton closeInventory(boolean closeInventory) {
        this.closeInventory = closeInventory;
        return this;
    }

    public void setCloseInventory(boolean closeInventory) {
        this.closeInventory = closeInventory;
    }

    public void setAction(ClickAction action) {
        this.action = action;
    }


}
