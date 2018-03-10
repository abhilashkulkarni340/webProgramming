package com.example.abhilashsk.storelocator;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {
    String[] items = {"Dosa", "Idli", "Coffee", "Maggi"} ;
    String[] prices = {"40", "30", "10", "35"} ;
    String[] weights = {"1 plate", "1 plate", "1 plate", "1 plate"} ;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Intent intent=getIntent();
        String shopname=intent.getStringExtra("ShopName");
        String address=intent.getStringExtra("Address");

        TextView shop_name=(TextView)findViewById(R.id.shop_name);
        TextView shop_address=(TextView)findViewById(R.id.shop_address_shp);

        shop_name.setText(shopname);
        shop_address.setText(address);

        final ArrayList<String> items2 = getInfo(items);
        final ArrayList<String> prices2 = getInfo(prices);
        final ArrayList<String> weights2 = getInfo(weights);
        final Bundle bundle=new Bundle();
        final CustomListShopProducts adapter = new CustomListShopProducts(ShopActivity.this, items2,prices2,weights2,bundle);
        list=(ListView)findViewById(R.id.list3);
        list.setAdapter(adapter);
        FloatingActionButton fab_cart = (FloatingActionButton) findViewById(R.id.fab_cart);
        fab_cart.setImageResource(R.drawable.ic_room_black_24dp);
        fab_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopActivity.this,CartActivity.class);
                intent.putExtra("bundle_cart_key",adapter.bundle_cart);
                startActivity(intent);
            }
        });
    }

    public ArrayList<String> getInfo(String[] arr){
        ArrayList<String> dynarr = new ArrayList<String>();
        for (String x:arr) {
            dynarr.add(x);
        }
        return dynarr;
    }
}
