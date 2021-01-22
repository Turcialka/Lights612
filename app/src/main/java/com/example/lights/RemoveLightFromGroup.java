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


public class RemoveLightFromGroup extends Fragment implements AdapterView.OnItemSelectedListener{

    Button deleteLightFromGroup;
    String userIdLTG;
    String nameSelectedLightToDelete;
    String serialSelectedLightToDelete;
    String nameSelectedGroupToDelete;

    List<String> lightNamesLightRemove = new ArrayList<>();
    List<String> lightSerialLightRemove = new ArrayList<>();
    List<String> groupNameLightRemove = new ArrayList<>();
    List<Group> allGroups = new ArrayList<>();
    List<String> groupsNamesWithBulb = new ArrayList<>();

    Spinner spinnerLightDelete;
    Spinner spinnerGroupDelete;

    NetworkHandler networkHandler;

    public RemoveLightFromGroup() {
    }

    public static RemoveLightFromGroup newInstance(String param1, String param2) {
        RemoveLightFromGroup fragment = new RemoveLightFromGroup();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_remove_light_from_group, container, false);
        ModelPanel activity = (ModelPanel) getActivity();

        lightNamesLightRemove = ((ModelPanel) getActivity()).lightNames;
        lightSerialLightRemove = ((ModelPanel) getActivity()).lightsSerial;
        groupNameLightRemove = ((ModelPanel) getActivity()).groupsName;
        allGroups = ((ModelPanel) getActivity()).groups;

        System.out.println("name " + lightNamesLightRemove+"serial "+ lightSerialLightRemove +"gr " +groupNameLightRemove);

        deleteLightFromGroup = v.findViewById(R.id.button_removeLightsFromGroup);

        networkHandler = new NetworkHandler();
        userIdLTG = activity.loggedUserId;

        spinnerLightDelete = (Spinner) v.findViewById(R.id.spinnerLightsDelete);
        spinnerGroupDelete = (Spinner) v.findViewById(R.id.spinnerGroupsDelete);


        ArrayAdapter<String> adapterLightNameDelete = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, lightNamesLightRemove);
        adapterLightNameDelete.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLightDelete.setAdapter(adapterLightNameDelete);

        spinnerLightDelete.setOnItemSelectedListener(this);
        spinnerGroupDelete.setOnItemSelectedListener(this);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        deleteLightFromGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(nameSelectedLightToDelete);
                System.out.println(serialSelectedLightToDelete);
                System.out.println(nameSelectedGroupToDelete);

                String url = networkHandler.makeUrl("/lights/removeLightToGroup", "serial="+serialSelectedLightToDelete,"name="+nameSelectedGroupToDelete ,"user_id="+userIdLTG);

                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response is: " + response);
                        if(response.equals("Removed")){
                            Toast.makeText(v.getContext(),"Usunięto pomyślnie!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), ModelPanel.class);
                            intent.putExtra("idUser", userIdLTG);
                            startActivity(intent);
                        }else{
                            Toast.makeText(v.getContext(),"Błąd usuwania urządzenia.", Toast.LENGTH_LONG).show();
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
        if(parent == spinnerLightDelete){
            nameSelectedLightToDelete = parent.getSelectedItem().toString();
            serialSelectedLightToDelete = lightSerialLightRemove.get(parent.getSelectedItemPosition());

            Light selectedLight = null;
            for (Light light: allGroups.get(0).getLights()) {
                if(light.getName().equals(nameSelectedLightToDelete))
                    selectedLight = light;
            }
            groupsNamesWithBulb.clear();
            for (Group group: allGroups) {
                boolean isLightInGroup = false;
                for (Light light:group.getLights()) {
                    if(light.getName().equals(selectedLight.getName()))
                        isLightInGroup = true;
                }
                if(isLightInGroup)
                    groupsNamesWithBulb.add(group.getName());
            }
            groupsNamesWithBulb.remove(0);
            ArrayAdapter<String> adapterGroupNameDelete = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, groupsNamesWithBulb);
            adapterGroupNameDelete.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGroupDelete.setEnabled(true);
            if(groupsNamesWithBulb.isEmpty())
                spinnerGroupDelete.setEnabled(false);
            spinnerGroupDelete.setAdapter(adapterGroupNameDelete);
        }
        else if(parent == spinnerGroupDelete){
            nameSelectedGroupToDelete = parent.getSelectedItem().toString();
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}