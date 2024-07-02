package com.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
public class Account extends AppCompatActivity {

    private EditText loginEditText, emailEditText, passwordEditText;
    private Button registerButton, buttonLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginEditText = findViewById(R.id.login);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        buttonLogin = findViewById(R.id.buttonLogin);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        // Hide the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void registerUser() {
        final String login = loginEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (login.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Account.this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.0.104/api/reg_api.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Account.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Account.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", login);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void loginUser() {
        final String username = loginEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Account.this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.0.104/api/auth_api.php"; // Replace with your server URL

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Проверяем ответ от сервера
                        if (response.equals("success")) {
                            UserManager userManager = UserManager.getInstance();
                            userManager.setUsername(username);
                            userManager.setEmail(email);
                            // Переход в новое активити при успешной авторизации
                            Intent intent = new Intent(Account.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Закрываем текущее активити
                        } else {
                            Toast.makeText(Account.this, "Ошибка аутентификации", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Account.this, "Ошибка: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);  // Add this line if your API requires email
                params.put("password", password);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}