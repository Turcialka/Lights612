package com.example.lights;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

//OD MICHALA

public class MainActivity extends AppCompatActivity {
    EditText etUsername, etPasswd;
    Button btLogin, btRegister;

    String outputString;
    String AES = "AES";
    String loggedUserId, loginUser;
    String nameLight, serialNum, nameGroup;
    NetworkHandler networkHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.et_username);
        etPasswd = findViewById(R.id.et_password);
        btLogin = findViewById(R.id.buttonlogin);
        btRegister = findViewById(R.id.buttonregister);

        networkHandler = new NetworkHandler();
        RequestQueue queue = Volley.newRequestQueue(this);

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                username_focus_change(view, hasFocus);
            }
        });

        etPasswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                password_focus_change(view, hasFocus);
            }
        });


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    outputString = encrypt(etPasswd.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = networkHandler.makeUrl("/users/checklog", "login="+etUsername.getText(), "password="+outputString);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response is: " + response);
                        if(response.startsWith("true")){
                            loginUser = etUsername.getText().toString(); //getting login from sign in panel
                            loggedUserId = response.substring(5); //gettind ID user from response string true+id
                            Intent intent = new Intent(MainActivity.this, modelPanel.class);
                            intent.putExtra("UsernameL", loginUser);
                            intent.putExtra("idUser", loggedUserId);
                            startActivity(intent);
                        }else{
                            Toast.makeText(v.getContext(),"Niepoprawny login lub hasło! Spróbuj ponownie!", Toast.LENGTH_SHORT);
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


        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegister = new Intent(MainActivity.this, Register.class);
                startActivity(intentRegister);
            }
        });
    }

    private String encrypt(String password)throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(password.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String password)throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return  secretKeySpec;
    }

    private void password_focus_change(View view, boolean hasFocus) {
        if(hasFocus){
            return;
        }
        if(etPasswd.getText().toString().trim().length() == 0){
            Toast.makeText(this, "Pole hasło nie może być puste", Toast.LENGTH_LONG).show();
        }
    }

    private void username_focus_change(View view, boolean hasFocus) {
        if(hasFocus){
            return;
        }
        if(etUsername.getText().toString().trim().length() == 0){
            Toast.makeText(this, "Pole login nie może być puste", Toast.LENGTH_LONG).show();
        }
    }

}