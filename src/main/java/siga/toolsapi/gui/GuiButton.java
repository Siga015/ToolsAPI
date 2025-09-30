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
    private ClickAction left_shiftAction;
    private ClickAction right_shiftAction;
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

    public ClickAction getRightShiftAction() {
        return right_shiftAction;
    }

    public ClickAction getLeftShiftAction() {
        return left_shiftAction;
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

    public GuiButton onRightShiftClick(ClickAction shiftAction) {
        this.right_shiftAction = shiftAction;
        return this;
    }

    public GuiButton onLeftShiftClick(ClickAction shiftAction) {
        this.left_shiftAction = shiftAction;
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

    public void setLeftShiftAction(ClickAction left_shiftAction) {
        this.left_shiftAction = left_shiftAction;
    }

    public void setRightShiftAction(ClickAction right_shiftAction) {
        this.right_shiftAction = right_shiftAction;
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
