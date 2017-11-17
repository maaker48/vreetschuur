package com.example.stephan.stephansvreetschuur;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public RequestQueue queue;
    public List<String> menuItems = new ArrayList<>();
    public ListView resultsListView;
    public TextView mTextMessage;
    public Basket shoppingList = Basket.getInstance();
    public TextView mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        resultsListView = findViewById(R.id.results_listview);

        mFab = findViewById(R.id.fab);
        mFab.setText(Integer.toString(shoppingList.getLength()));

        BottomNavigationView navigation = (findViewById(R.id.navigation));
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        retrievePrefs();
        init();
    }

    private void retrievePrefs () {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = mPrefs.getString("shoppingList", "");
        if(json != "") {
            Basket restoredJson = gson.fromJson(json, Basket.class);
            List<Item> tempList = restoredJson.getItems();
            shoppingList.setItems(tempList);
            mFab.setText(Integer.toString(shoppingList.getLength()));
        }
    }

    private void init() {

        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "https://resto.mprog.nl/categories";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray categories  = response.getJSONArray("categories");

                            for (int i = 0; i < categories.length(); i++) {
                                menuItems.add(categories.get(i).toString());
                            }
                            updateListView();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);

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
                    Intent intent_order = new Intent(getBaseContext(), Orders.class);
                    startActivity(intent_order);
                    return true;
            }
            return false;
        }
    };

    private void updateListView() {
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.text1, menuItems);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String value = menuItems.get(position);
                Log.d("Error.Response", value);

                Intent intent = new Intent(getBaseContext(), SubMenu.class);
                intent.putExtra("SELECTED_MENU", value);
                startActivity(intent);
            }
        });
    }

}