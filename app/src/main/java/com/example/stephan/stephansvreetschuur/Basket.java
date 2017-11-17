package com.example.stephan.stephansvreetschuur;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Stephan on 16-11-2017.
 */

public class Basket {

    public List<Item> currentOrders = new ArrayList<>();

    public void deleteItem (Item item) {
        this.currentOrders.remove(item);
    }

    public void clearOrder () {this.currentOrders = new ArrayList<>();}

    public void addItem (Item item) {
        this.currentOrders.add(item);
    }

    public List<Item> getItems () {return this.currentOrders;}

    public void setItems (List<Item> orderList) {this.currentOrders = orderList;}

    public int getLength () {return this.currentOrders.size();}

    private static final Basket basketList = new Basket();

    public static Basket getInstance () {return basketList;}

}
