package com.example.lights;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Register extends AppCompatActivity {

    EditText reg_mail,reg_username, reg_password;
    Button buttonREGISTERuser;

    String outputString;
    String AES = "AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        NetworkHandler networkHandler = new NetworkHandler();
        RequestQueue queue = Volley.newRequestQueue(this);

        reg_mail = findViewById(R.id.reg_mail);
        reg_username = findViewById(R.id.reg_username);
        reg_password = findViewById(R.id.reg_password);
        buttonREGISTERuser = findViewById(R.id.buttonREGISTERuser);

        buttonREGISTERuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkMail(v) && checkLogin(v) && checkPasswd(v)) {
                    try {
                        outputString = encrypt(reg_password.getText().toString());
                        System.out.println(outputString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String url = networkHandler.makeUrl("/users/add", "login="+reg_username.getText(),
                            "password="+outputString, "email="+reg_mail.getText());
                    System.out.println(url);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("Response is: " + response);
                            if(response.equals("Username_exist") || response.equals("Email_exist")){
                                Toast.makeText(v.getContext(), "Dane niepoprawne! Proszę sprawdzić wpisane dane.",
                                        Toast.LENGTH_LONG).show();
                                System.out.println("Dane niepoprawne! Proszę sprawdzić wpisane dane.");
                            }else{
                                Toast.makeText(v.getContext(), "Zarejestrowano pomyślnie!", Toast.LENGTH_SHORT).show();
                                Intent mainActivity = new Intent(Register.this, MainActivity.class);
                                startActivity(mainActivity);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("That didn't work!");
                        }
                    });
                    queue.add(stringRequest);
                }
            }
        });
    }

    private String encrypt(String password) throws Exception{
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(password.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);

        return encryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        return  secretKeySpec;
    }


    private boolean checkMail(View view){
        String getEmail = reg_mail.getText().toString();

        if (!isEmailValid(getEmail)) {
            Toast.makeText(view.getContext(), "Email jest niepoprawny!", Toast.LENGTH_LONG).show();

            return false;
        } else if (getEmail.equals("")) {
            Toast.makeText(view.getContext(), "Pole email nie może być puste!", Toast.LENGTH_LONG).show();

            return false;
        } else {
            System.out.println("Email dobrze");
            return true;
        }
    }

    private boolean checkLogin(View view){
        String getUsername = reg_username.getText().toString();

        if (getUsername.equals("")) {
            Toast.makeText( view.getContext(),"Pole login nie może być puste!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            System.out.println("username dobrze podany");
            return true;
        }
    }

    private boolean checkPasswd(View view){
        String getPasswd = reg_password.getText().toString();


        if (getPasswd.equals("")) {
            Toast.makeText(view.getContext(), "Pole hasło nie może być puste!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            System.out.println("haslo dobrze podany");
            return true;

        }
    }

    boolean isEmailValid(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}