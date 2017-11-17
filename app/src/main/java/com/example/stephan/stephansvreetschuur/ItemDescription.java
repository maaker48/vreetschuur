package com.example.stephan.stephansvreetschuur;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ItemDescription extends AppCompatActivity {

    public Button buyButton;
    public TextView itemDesc;
    public TextView itemPrice;
    public TextView itemName;
    public ImageView itemImage;
    public Item selectedItem;
    public Basket shoppingList = Basket.getInstance();
    public TextView mTextMessage;
    public TextView mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);
        selectedItem = (Item) getIntent().getSerializableExtra("SELECTED_ITEM");
        itemImage = findViewById(R.id.itemImage);
        itemPrice = findViewById(R.id.price);
        itemDesc = findViewById(R.id.itemDesc);
        itemName = findViewById(R.id.itemName);
        buyButton = findViewById(R.id.buyButton);
        buyButton.setOnClickListener(buyAction);

        mTextMessage = findViewById(R.id.message);
        mFab = findViewById(R.id.fab);
        mFab.setText(Integer.toString(shoppingList.getLength()));
        BottomNavigationView navigation = (findViewById(R.id.navigation));
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        populateData();
    }

    View.OnClickListener buyAction = new View.OnClickListener() {
        public void onClick (View v) {
            Context context = getApplicationContext();
            CharSequence text = selectedItem.name + " has been added";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            shoppingList.addItem(selectedItem);
            mFab.setText(Integer.toString(shoppingList.getLength()));

            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(shoppingList);
            Log.d("saving list", json);
            editor.putString("shoppingList", json);
            editor.commit();
            editor.apply();
        }
    };

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

    public void populateData (){
        new ImageLoadTask(selectedItem.image_url, itemImage).execute();
        itemDesc.setText(selectedItem.description);
        itemPrice.setText(Integer.toString(selectedItem.price) +"\u20ac");
        itemName.setText(selectedItem.name);
        Log.d("Image", selectedItem.image_url);
    }

    //https://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
