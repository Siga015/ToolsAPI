package siga.toolsapi.gui;

import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIShape {

    private static Map<Character, ItemStack> globalItems = new HashMap<>();
    private Map<Character, ItemStack> items = new HashMap<>();
    private Map<Character, GuiButton> buttons = new HashMap<>();
    private final List<String> rows;

    public int rowsLength;


    public GUIShape(String... rows) {
        Validate.notNull(rows, "Must provide a shape");

        Validate.isTrue(rows.length > 0 && rows.length < 10, "GUI menu should have from 1 to 9 rows, not ", rows.length);

        int lastLen = -1;
        for (String row : rows) {
            Validate.notNull(row, "Shape cannot have null rows");
            Validate.isTrue(row.length() > 0 && row.length() < 10, "GUI row should have from 1 to 9 characters, not ", row.length());

            Validate.isTrue(lastLen == -1 || lastLen == row.length(), "GUI menu must be rectangular");
            lastLen = row.length();
        }

        HashMap<Character, ItemStack> newItems = new HashMap<>();
        HashMap<Character, GuiButton> newButtons = new HashMap<>();
        for (String row : rows) {
            for (Character c : row.toCharArray()) {

                if (!globalItems.isEmpty()) {
                    if (globalItems.containsKey(c)) {
                        newItems.put(c, globalItems.get(c));
                    }
                }

                newItems.put(c, items.get(c));
                newButtons.put(c, buttons.get(c));
            }
        }
        this.items = newItems;
        this.buttons = newButtons;

        this.rows = Arrays.asList(rows);
        this.rowsLength = rows.length;
    }


    public void setItem(char key, ItemStack item) {
        Validate.isTrue(items.containsKey(key), "Symbol does not appear in the shape: ", key);

        items.put(key, item);
    }



    public GUIShape setButton(char key, GuiButton button) {
        Validate.isTrue(buttons.containsKey(key), "Symbol does not appear in the shape: ", key);

        buttons.put(key, button);
        return this;
    }

    public static void setGlobalItem(char key, ItemStack item) {
        globalItems.put(key, item);
    }


    public int getRowsLength() {
        return rowsLength;
    }


    public Map<Character, ItemStack> getItems() {
        return items;
    }

    public Map<Character, GuiButton> getButtons() {
        return buttons;
    }

    public List<String> getRows() {
        return rows;
    }



}
