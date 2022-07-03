package com.example.cm07project;

public class Products {
    /** Represents com.example.cm07project.Products on Firebase DB*/

    public String id;
    public String userID;
    public String item;
    public String desc;
    public String category;
    public int quantity;
    public String photoid;

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String photoid) {
        this.photoid = photoid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDesc() {
        return desc;
    }

    public String getUserID() {
        return userID;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Products(String id, String userID, String item, String desc, String category, int quantity, String photoId) {
        this.id =id;
        this.userID = userID;
        this.item = item;
        this.desc = desc;
        this.category = category;
        this.quantity = quantity;
        this.photoid = photoId;
    }
}
