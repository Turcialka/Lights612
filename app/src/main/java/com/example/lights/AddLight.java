package com.example.lights;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddLight#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddLight extends Fragment{

    Button saveLight;
    EditText name;
    EditText serial;
    String userId;

    NetworkHandler networkHandler;

    public AddLight() {
        // Required empty public constructor
    }

    public static AddLight newInstance(String param1, String param2) {
        AddLight fragment = new AddLight();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          //  networkHandler = new NetworkHandler();
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_light, container, false);
        saveLight = v.findViewById(R.id.button_addL);
        name = v.findViewById(R.id.addLightEditTextName);
        serial = v.findViewById(R.id.addLightEditTextSerialNumber);

        ModelPanel activity = (ModelPanel) getActivity();

        networkHandler = new NetworkHandler();
        userId = activity.loggedUserId;

        Toast.makeText(activity, userId , Toast.LENGTH_SHORT).show();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        saveLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = networkHandler.makeUrl("/lights/add", "serial="+serial.getText(),"name="+name.getText() ,"user_id="+userId);
               // System.out.println(url);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response is: " + response);
                        if(response.equals("Saved")){
                            Toast.makeText(v.getContext(),"Dodano pomyślnie urządzenie!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), ModelPanel.class);
                            intent.putExtra("idUser", userId);
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

}

