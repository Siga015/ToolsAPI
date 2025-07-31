package siga.toolsapi.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import siga.toolsapi.util.ColorTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class GuiButton {

    private final ItemStack item;
    private final String title;
    private ClickAction action;
    private ClickAction shiftAction;
    private boolean closeInventory;
    private LoreProvider loreProvider;


    public GuiButton(String title, Material material) {
        this.title = title;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorTranslator.translate(title));

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

    public void setEnchantVisible(boolean flag) {

        ItemMeta meta = item.getItemMeta();

        if (flag) {
            meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        else {
            meta.removeEnchantments();
        }


        item.setItemMeta(meta);
    }


    public boolean isEnchantVisible() {
        ItemMeta meta = item.getItemMeta();

        return meta.hasEnchant(Enchantment.LUCK_OF_THE_SEA);
    }

    public void setLore(LoreProvider loreProvider) {
        this.loreProvider = loreProvider;
    }

    public ItemStack getItem() {
        return item;
    }

    public ClickAction getAction() {
        return action;
    }

    public ClickAction getShiftAction() {
        return shiftAction;
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

    public GuiButton onShiftClick(ClickAction shiftAction) {
        this.shiftAction = shiftAction;
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

    public void setShiftAction(ClickAction shiftAction) {
        this.shiftAction = shiftAction;
    }


    public void updateLore(Player player) {
        if (loreProvider == null) return;

        List<String> lore = new ArrayList<>(loreProvider.getLore(player));
        lore.replaceAll(ColorTranslator::translate);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
    }


}
