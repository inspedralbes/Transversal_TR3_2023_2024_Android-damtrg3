package com.mygdx.game.utils;

public class Product {
    private int id;
    private String name;
    private double list_price;
    private String type;
    private String image_1920;

    public Product(String name, double list_price, String type, String image_1920) {
        this.name = name;
        this.list_price = list_price;
        this.type = type;
        this.image_1920 = image_1920;
    }
    public Product (int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public double getList_price() {
        return list_price;
    }
    public void setList_price(double list_price) {
        this.list_price = list_price;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getImage_1920() {
        return image_1920;
    }
    public void setImage_1920(String image_1920) {
        this.image_1920 = image_1920;
    }
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", list_price=" + list_price +
                ", image_1920='" + image_1920 + '\'' +
                '}';
    }

}