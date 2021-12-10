package com.hust.temp;

import android.app.ProgressDialog;
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
    private SharedPreferences.Editor preferencesEditor;
    private EditText username, password;
    private CircularProgressButton btnLogin;
    private String usernameLogin, passLogin;
    private SharedPreferences mPreferences;
    private String isSignedIn = "";
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_login);

        mPreferences = getSharedPreferences(Constant.SHARED_PROFILE, MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();
        isSignedIn = mPreferences.getString(Constant.IS_LOGGED_IN, Constant.FALSE);
        if (isSignedIn.equals(Constant.TRUE)) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
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
        btnLogin = findViewById(R.id.cirLoginButton);
    }

    private void login() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.ROOT_URL_LOGIN,
                response -> {
                    Log.w("anyText", response);
                    loading.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString(Constant.SUCCESS);

                        if (success.equals("1")) {
                            String id = jsonObject.getString(Constant.KEY_ID);
                            String name = jsonObject.getString(Constant.KEY_FULL_NAME);
                            String username = jsonObject.getString(Constant.KEY_USERNAME);
                            String role = jsonObject.getString(Constant.KEY_ROLE);
                            preferencesEditor.putString(Constant.IS_LOGGED_IN, Constant.TRUE);
                            preferencesEditor.putString(Constant.KEY_ID_SIGNED, id);
                            preferencesEditor.putString(Constant.KEY_FULL_NAME_SIGNED, name);
                            preferencesEditor.putString(Constant.KEY_USERNAME_SIGNED, username);
                            preferencesEditor.putString(Constant.KEY_ROLE_SIGNED, role);
                            preferencesEditor.apply();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        if (success.equals("0")) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_valid_user), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,
                                getResources().getString(R.string.can_trans_data),
                                Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            loading.dismiss();
            Toast.makeText(this, getResources().getString(R.string.server_error) + error, Toast.LENGTH_LONG).show();
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
        loading = ProgressDialog.show(this,
                "",
                getResources().getString(R.string.login), false, false);
    }
}
