package com.example.abhilashsk.storelocator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import android.Manifest;


import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static com.example.abhilashsk.storelocator.LoginActivity.MyPREFERENCES;

public class Dashboard2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView list1;
    CustomList adapter;
    Double my_lat,my_lon;
    ArrayList<String> sn,loc,cat;
    ArrayList<Integer> dis2;
    String[] shopnames = {"Vidhyarthi Khana", "Oasis", "Meghana's Foods", "Truffles","Shop Rite",
            "Mantri Square","Orion Mall","Chungs","IISc","CPRI","Lulu","Reliance Fresh",
    "Byraveshwara Rice Traders"} ;
    String[] locations = {"Basavangudi", "Banashankari", "Jayanagar", "Kormangala","Jalahalli",
            "Malleshwaram","Yeshwanthpur","Malleshwaram,18th cross", "CV Raman Road",
            "Ashwath Nagar,Armane Nagar","MS Palya","509, Vidyaranyapura","MS Palya"};
    String[] categories={"Food","Food","Groceries","Food","Groceries",
            "Groceries","Food","Food","Groceries","Food","Groceries","Groceries","Food"};
    String[] category_types={"All","Food","Groceries"};
    Integer[] tabs_list={R.id.tab1,R.id.tab2,R.id.tab3};
    Integer[] listView_list={R.id.list2,R.id.list3,R.id.list4};
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        checkSessionDashboard();
        /*GPSManager gps = new GPSManager(
                Dashboard2Activity.this);
        gps.start();*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String cart = sharedpreferences.getString("cartKey","");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_shopping_cart_black_24dp);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Your cart has "+cart+" items.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //Filtering by location
        Tracer gps_for_filter = new Tracer(Dashboard2Activity.this);
        if (gps_for_filter.getLocation() != null) {
            my_lat = gps_for_filter.getLatitude();
            my_lon = gps_for_filter.getLongitude();
            Toast.makeText(this,"Your location is: "+ my_lat + " " + my_lon,Toast.LENGTH_LONG).show();;
            Log.d("onCreate MarkersMap", "Your location is " + my_lat + " " + my_lon);
        } else {
            //gps.showSettingAlert();
            my_lat = 12.942898;
            my_lon = 77.56819659999996;
            Toast.makeText(this,"Your location cannot be found",Toast.LENGTH_LONG).show();;
            Log.d("onCreate MarkersMap", "Your location cannot be found");
        }
        Location loc1 = new Location("");
        loc1.setLatitude(my_lat);
        loc1.setLongitude(my_lon);
        ArrayList<Float> dis=new ArrayList<>();
        dis2=new ArrayList<>();
        for(int i=0;i<shopnames.length;i++) {


            GeocodingLocation locationAddress = new GeocodingLocation();
            Bundle bundle = locationAddress.getAddressFromLocation(locations[i],
                    getApplicationContext());
            Double lat_for_filter = Double.parseDouble(bundle.getString("latitude"));
            Double lon_for_filter = Double.parseDouble(bundle.getString("longitude"));
            Log.d(shopnames[i], lat_for_filter + " " + lon_for_filter);

            Location loc2 = new Location("");
            loc2.setLatitude(lat_for_filter);
            loc2.setLongitude(lon_for_filter);

            float distanceInMeters = loc1.distanceTo(loc2);

            Log.d("Distance from "+shopnames[i], "" + distanceInMeters / 1000);
            dis.add(distanceInMeters/1000);
            if(distanceInMeters/1000<=100){
                dis2.add(Math.round(distanceInMeters/1000));
            }
        }

        ListView list_for_dis=(ListView)findViewById(R.id.list_for_dis);
        ArrayAdapter<Float> adapter2=new ArrayAdapter<Float>(this,
                android.R.layout.simple_list_item_1,
                dis);
        list_for_dis.setAdapter(adapter2);

        //Button to find shops by location
        final ArrayList<String> shopName = getInfoForTabs(shopnames,categories,"All",dis);
        final ArrayList<String> location = getInfoForTabs(locations,categories,"All",dis);

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setImageResource(R.drawable.ic_room_black_24dp);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard2Activity.this,MarkersMapActivity.class);
                intent.putExtra("shopname_key",shopName);
                intent.putExtra("address_key",location);
                startActivity(intent);
            }
        });

        //Filtering by categories
        TabHost host = (TabHost)findViewById(R.id.tab_host);
        host.setup();

        for(int i=0;i<category_types.length;i++){
            sn=getInfoForTabs(shopnames,categories,category_types[i],dis);
            loc=getInfoForTabs(locations,categories,category_types[i],dis);
            cat=getInfoForTabs(categories,categories,category_types[i],dis);


            adapter=new CustomList(Dashboard2Activity.this,sn,loc,cat,dis2);
            list1=(ListView)findViewById(listView_list[i]);
            list1.setAdapter(adapter);

            TabHost.TabSpec spec = host.newTabSpec(category_types[i]);
            spec.setContent(tabs_list[i]);
            spec.setIndicator(category_types[i]);
            host.addTab(spec);
        }

        TabHost.TabSpec spec = host.newTabSpec("Distance");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Distance");
        host.addTab(spec);



        /*CustomList adapter = new CustomList(Dashboard2Activity.this, shopName,location,category);
        list1=(ListView)findViewById(R.id.list2);
        list1.setAdapter(adapter);

        ArrayList<String> sn1=getInfoForTabs(shopnames,categories,"Food");
        ArrayList<String> loc1=getInfoForTabs(locations,categories,"Food");
        ArrayList<String> cat1=getInfoForTabs(categories,categories,"Food");

        CustomList adapter1 = new CustomList(Dashboard2Activity.this, sn1,loc1,cat1);
        list2=(ListView)findViewById(R.id.list3);
        list2.setAdapter(adapter1);

        ArrayList<String> sn2=getInfoForTabs(shopnames,categories,"Groceries");
        ArrayList<String> loc2=getInfoForTabs(locations,categories,"Groceries");
        ArrayList<String> cat2=getInfoForTabs(categories,categories,"Groceries");

        CustomList adapter2 = new CustomList(Dashboard2Activity.this, sn2,loc2,cat2);
        list3=(ListView)findViewById(R.id.list4);
        list3.setAdapter(adapter2);*/

        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        //myRef.setValue("Hello, World!");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("DBFireBase","Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DBFirebase Error", "Failed to read value.", error.toException());
            }
        });*/



        /*TabHost.TabSpec spec = host.newTabSpec("ALL");
        spec.setContent(R.id.tab1);
        spec.setIndicator("ALL");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Food");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Food");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Groceries");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Groceries");
        host.addTab(spec);*/

    }


    protected void onResume(){
        super.onResume();
        Log.d("DASHBOARD","Dashboard2Activity resumed");
        checkSessionDashboard();
        /*GPSManager gps = new GPSManager(
                Dashboard2Activity.this);
        gps.start();*/
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            onBackPressedAfter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ArrayList<String> getInfo(String[] arr){
        ArrayList<String> dynarr = new ArrayList<String>();
        for (String x:arr) {
            dynarr.add(x);
        }
        return dynarr;
    }

    public ArrayList<String> getInfoForTabs(String[] arr_info,String[] cat_arr,String cat,ArrayList<Float> distance){
        ArrayList<String> info=new ArrayList<>();
        if(cat=="All"){
            for(int i=0;i<cat_arr.length;i++)
                if(distance.get(i)<100)
                    info.add(arr_info[i]);
        }else {
            for (int i = 0; i < cat_arr.length; i++) {
                if (cat_arr[i] == cat && distance.get(i)<100) {
                    info.add(arr_info[i]);
                }
            }
        }
        return info;
    }

    public void checkSessionDashboard(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        try{
            String user = sharedpreferences.getString("usernameKey","");
            String pass = sharedpreferences.getString("passwordKey","");
            if(user.isEmpty()&&pass.isEmpty()){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }catch (Exception e){
            Log.d("Session Error",e.toString());
        }
    }

    public void onBackPressedAfter(){
        Log.d("DASHBOARD","back key pressed");
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}
