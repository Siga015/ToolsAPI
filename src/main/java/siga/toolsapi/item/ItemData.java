package siga.toolsapi.item;

import java.util.List;

public class ItemData {

    private final String name;
    private final String modelName;
    private final String category;
    private final List<String> lore;

    public ItemData(String name, String modelName, String category, List<String> lore) {
        this.name = name;
        this.modelName = modelName;
        this.category = category;
        this.lore = lore;
    }

    public String getName() {
        return name;
    }

    public String getModelName() {
        return modelName;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getLore() {
        return lore;
    }
}