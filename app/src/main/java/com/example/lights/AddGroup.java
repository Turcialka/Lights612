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

public class AddGroup extends Fragment {

    private Button saveGroup;
    private EditText nameGroup;
    private String newGroupUserId;
    private NetworkHandler networkHandler;

    public AddGroup() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_group, container, false);

        saveGroup = v.findViewById(R.id.button_addNewGroup);
        nameGroup = v.findViewById(R.id.addNewGroupName);

        ModelPanel activity = (ModelPanel) getActivity();

        networkHandler = new NetworkHandler();
        newGroupUserId = activity.loggedUserId;

        Toast.makeText(activity, newGroupUserId, Toast.LENGTH_SHORT).show();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        saveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = networkHandler.makeUrl("/groups/add",
                        "name=" + nameGroup.getText(), "user_id=" + newGroupUserId);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("Saved")) {
                                    Toast.makeText(v.getContext(), "Grupa dodana pomyślnie!",
                                            Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getContext(), ModelPanel.class);
                                    intent.putExtra("idUser", newGroupUserId);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(v.getContext(), "Błąd dodawania nowej grupy.",
                                            Toast.LENGTH_LONG).show();
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