package com.hust.temp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hust.temp.Common.Constant;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences.Editor preferencesEditor;
    String URL_LOGIN = "https://protocoderspoint.com/php/login.php";
    EditText username, password;
    CircularProgressButton btnLogin;
    String usernameLogin, passLogin;
    SharedPreferences mPreferences;
    String sharedprofFile="com.protocoderspoint.registration_login";
    String is_signed_in="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_login);

        mPreferences=getSharedPreferences(sharedprofFile,MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();
        is_signed_in = mPreferences.getString(Constant.IS_LOGGED_IN,Constant.FALSE);
        if(is_signed_in.equals(Constant.TRUE))
        {
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        findViewByIdForWidget();
        setEventClick();
    }

    private void setEventClick() {
        btnLogin.setOnClickListener(v -> {
            usernameLogin = username.getText().toString().trim();
            passLogin = password.getText().toString().trim();
            if (usernameLogin.isEmpty() || passLogin.isEmpty()) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.message_fill_all_text), Toast.LENGTH_SHORT).show();
            } else {
                login();
            }
        });
    }

    private void findViewByIdForWidget() {
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        btnLogin = (CircularProgressButton) findViewById(R.id.cirLoginButton);
    }

    private void login() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                response -> {
                    Log.e("anyText", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        String message = jsonObject.getString("message");
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String username = jsonObject.getString("username");
                        String role = jsonObject.getString("role");
                        if (success.equals("1")) {
                            Toast.makeText(getApplicationContext(), "Logged In  Success", Toast.LENGTH_LONG).show();
                            preferencesEditor.putString(Constant.IS_LOGGED_IN, "true");
                            preferencesEditor.putString("SignedInUserID", id);
                            preferencesEditor.putString("SignedInName", name);
                            preferencesEditor.putString("SignedInUsername", username);
                            preferencesEditor.putString("SignedInUserRole", role);
                            preferencesEditor.apply();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        if (success.equals("0")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                                pdDialog.dismiss();
                        }
                        if (success.equals("3")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                                pdDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Login Error !1" + e, Toast.LENGTH_LONG).show();
                    }
                }, error -> {
//                    pdDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Login Error !2" + error, Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.KEY_USERNAME, usernameLogin);
                params.put(Constant.KEY_PASSWORD, passLogin);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
