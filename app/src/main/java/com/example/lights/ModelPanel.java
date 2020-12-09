package com.example.lights;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
//
public class ModelPanel extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    String User, loggedUserId;
    NetworkHandler networkHandler;
    List<Group> groups;
    List<String> lightNames = new ArrayList<>();
    List<String> lightsSerial = new ArrayList<>();
    List<String> groupsName = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_panel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        networkHandler = new NetworkHandler();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_light, R.id.nav_groups, R.id.nav_schedule)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        User = (String) getIntent().getExtras().getString("UsernameL"); //getting login, ID from activity modelPanel
        loggedUserId = (String) getIntent().getExtras().getString("idUser");

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getGroups());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.model_panel, menu); // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add_light:
                NavHostFragment.findNavController(getVisibleFragment()).navigate(R.id.ac_g_addLight);
                break;
            case R.id.add_group:
                NavHostFragment.findNavController(getVisibleFragment()).navigate(R.id.ac_g_addNewGroup);
                break;
            case R.id.add_light_to_group:
                NavHostFragment.findNavController(getVisibleFragment()).navigate(R.id.ac_g_light_to_group);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = ModelPanel.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public StringRequest getGroups(){

        String url = networkHandler.makeUrl("/groups/get", "user_id="+loggedUserId);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Response is: " + response);
                Gson gson = new Gson();
                Type groupListType = new TypeToken<ArrayList<Group>>(){}.getType();
                groups = gson.fromJson(response, groupListType);
                for (Group group:groups){
                    groupsName.add(group.getName());
                }
                for(Light light:groups.get(0).getLights()){
                    lightNames.add(light.getName());
                    lightsSerial.add(light.getSerial());
                }
                NavHostFragment.findNavController(getVisibleFragment()).navigate(R.id.action_placeholderFragment_to_nav_light);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
                Log.e("Volly Error", error.toString());
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                }
            }
        });
        return stringRequest;
    }
}
