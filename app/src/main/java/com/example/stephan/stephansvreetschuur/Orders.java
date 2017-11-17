package com.example.stephan.stephansvreetschuur;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Orders extends AppCompatActivity {
    public Basket shoppingList = Basket.getInstance();
    public ListView ordersListView;
    public List<String> orders = new ArrayList<>();
    public List<Item> orderList = new ArrayList<>();
    public TextView mTextMessage;
    public ArrayAdapter adapter;
    public RequestQueue queue;
    public TextView mFab;
    public Integer totalMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ordersListView = findViewById(R.id.order_listview);
        orderList = shoppingList.getItems();
        mFab = findViewById(R.id.fabtotal);

        queue = Volley.newRequestQueue(this);
        BottomNavigationView navigation = (findViewById(R.id.navigation));
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Menu menu = navigation.getMenu();
        menu.findItem(R.id.navigation_dashboard).setIcon(R.drawable.ic_send_black_24dp).setTitle("Send");

        updateListView();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_dashboard:
                    postOrderList();
                    return true;
            }
            return false;
        }
    };

    private void postOrderList() {
        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "https://resto.mprog.nl/order";


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Context context = getApplicationContext();
                            JSONObject parsedObject = new JSONObject(response);
                            String time = parsedObject.getString("preparation_time");
                            CharSequence text = "Your order will be ready in " +time +" minutes! " +
                                    "and your order total is "+Integer.toString(totalMoney) +"\u20ac";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            shoppingList.clearOrder();
                            adapter.clear();

                            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = mPrefs.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(shoppingList);
                            Log.d("saving list", json);
                            editor.putString("shoppingList", json);
                            editor.commit();
                            editor.apply();
                            updateListView();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                for(Item order: orderList) {
                    params.put(Integer.toString(order.id), "");
                }

                return params;
            }
        };
        queue.add(postRequest);

    }

    private void refreshList(int position) {
        shoppingList.deleteItem(orderList.get(position));
        orderList = shoppingList.getItems();
        adapter.remove(adapter.getItem(position));

        Context context = getApplicationContext();
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_res), MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(orderList);
        editor.putString("shoppingList", json);
        editor.commit();
        updateListView();
    }

    private void updateListView() {
        orderList = shoppingList.getItems();
        totalMoney = 0;

        for(Item order: orderList) {
            orders.add(" X - " +order.name +" - "+Integer.toString(order.price) +"\u20ac");
            totalMoney += order.price;
        }
        mFab.setText(Integer.toString(totalMoney) +"\u20ac");
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.text1, orders);
        ordersListView.setAdapter(adapter);
        ordersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                refreshList(position);
            }
        });
    }
}
