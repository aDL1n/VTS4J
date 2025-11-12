package dev.adlin.vts4j.core.hotkey;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

public class Hotkey {
    @SerializedName("name")
    private final String name;
    @SerializedName("type")
    private final String type;
    @SerializedName("description")
    private final String description;
    @SerializedName("hotkeyID")
    private final String id;
    @Nullable
    @SerializedName("itemInstanceID")
    private final String itemInstanceId;

    public Hotkey(String name, String type, String description, String id, @Nullable String itemInstanceId) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.id = id;
        this.itemInstanceId = itemInstanceId;
    }

    public Hotkey(String name, String type, String description, String id) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.id = id;
        this.itemInstanceId = null;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public String getItemInstanceId() {
        return itemInstanceId;
    }

    @Override
    public String toString() {
        return "[name=" + name + ", type=" + type + ", description=" + description + ", id=" + id + "]";
    }
}
