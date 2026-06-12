package fpoly.vinv01.assignmentapi.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Dish implements Serializable {
    @SerializedName("_id")
    private String id;
    private String name;
    private double price;
    private String category;
    @SerializedName("image")
    private String imageUrl;

    private String description;
    private int quantity = 1;

    public Dish(String id, String name, double price, String imageUrl, String category, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
        this.quantity = 1;
    }

    public Dish() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}