package com.example.lights;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;


public class AddLightToGroup extends Fragment implements AdapterView.OnItemSelectedListener{

    Button saveLightInGroup;
    String userIdLTG;
    String nameSelectedLight;
    String serialSelectedLight;
    String nameSelectedGroup;


    List<String> lightNamesLightToGroup= new ArrayList<>();
    List<String> lightSerialLightToGroup = new ArrayList<>();
    List<String> groupNameLightToGroup = new ArrayList<>();

    Spinner spinnerLight;
    Spinner spinnerGroup;

    NetworkHandler networkHandler;


    public AddLightToGroup(){

    }

    public static AddLightToGroup newInstance(String param1, String param2) {
        AddLightToGroup fragment = new AddLightToGroup();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_light_to_group, container, false);
        ModelPanel activity = (ModelPanel) getActivity();

        lightNamesLightToGroup = ((ModelPanel) getActivity()).lightNames;
        lightSerialLightToGroup = ((ModelPanel) getActivity()).lightsSerial;
        groupNameLightToGroup = ((ModelPanel) getActivity()).groupsName;



    //    System.out.println("mValues: "+ );
      //  System.out.println("mValues: "+ mGroups);


        saveLightInGroup = v.findViewById(R.id.button_addLightsToGroup);

        networkHandler = new NetworkHandler();
        userIdLTG = activity.loggedUserId;

        spinnerLight = (Spinner) v.findViewById(R.id.spinnerLights);
        spinnerGroup = (Spinner) v.findViewById(R.id.spinnerGroups);


        ArrayAdapter<String> adapterLightName = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, lightNamesLightToGroup);
        adapterLightName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLight.setAdapter(adapterLightName);

        ArrayAdapter<String> adapterGroupName = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, groupNameLightToGroup);
        adapterGroupName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapterGroupName);


        spinnerLight.setOnItemSelectedListener(this);
        spinnerGroup.setOnItemSelectedListener(this);


        RequestQueue queue = Volley.newRequestQueue(getContext());

        saveLightInGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println(nameSelectedLight);
                System.out.println(serialSelectedLight);
                System.out.println(nameSelectedGroup);

                String url = networkHandler.makeUrl("/lights/addLightToGroup", "serial="+serialSelectedLight,"name="+nameSelectedGroup ,"user_id="+userIdLTG);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response is: " + response);
                        if(response.equals("saved")){
                            Toast.makeText(v.getContext(),"Dodano pomyślnie!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), ModelPanel.class);
                            intent.putExtra("idUser", userIdLTG);
                            startActivity(intent);
                        }else{
                            Toast.makeText(v.getContext(),"Błąd dodawania urządzenia.", Toast.LENGTH_LONG).show();
                        }
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
                queue.add(stringRequest);
            }
        });


        return v;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int L = spinnerLight.getSelectedItemPosition();
        nameSelectedLight = lightNamesLightToGroup.get(L);
        serialSelectedLight = lightSerialLightToGroup.get(L);


        int G = spinnerGroup.getSelectedItemPosition();
        nameSelectedGroup = groupNameLightToGroup.get(G);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}