package com.example.stephan.stephansvreetschuur;

import java.io.Serializable;

/**
 * Created by Stephan on 16-11-2017.
 */

public class Item implements Serializable {
    String category;
    String description;
    int price;
    String image_url;
    int id;
    String name;

    public Item(String category, String description, int price, String image_url, int id, String name)
    {
        this.category = category;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
        this.id = id;
        this.name = name;
    }
}