package com.example.library;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
//asdasdasdadasdasasdas
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Book_content extends AppCompatActivity {

    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    int BookId;
    String userEmail;
    private RequestQueue requestQueue;
    private String checkApiUrl = "http://192.168.0.104/api/favorite_books_api.php";
    private String addApiUrl = "http://192.168.0.104/api/add_to_fav_java.php";
    private String removeApiUrl = "http://192.168.0.104/api/remove_from_fav_java.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_book_content);
        requestQueue = Volley.newRequestQueue(this);
        Bundle arguments = getIntent().getExtras();
        BookId = Integer.parseInt(arguments.get("id").toString());
        TextView booktext = findViewById(R.id.textView1);
        // Получаем экземпляр синглтона UserManager
        UserManager userManager = UserManager.getInstance();
        // Получаем значение почты
        userEmail = userManager.getEmail();



        // Инициализация очереди запросов Volley
        requestQueue = Volley.newRequestQueue(this);




        ImageButton switch_to_another_window1 = findViewById(R.id.imagebutton1);
        switch_to_another_window1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Book_content.this, MainActivity.class);
                startActivity(intent);
                finish(); //закрытие старого окна
            }
        });

        Button book_description_and_back = findViewById(R.id.button3); //я оставлю пока но это нужно реализовать через смену текста а не чрез новое окно
        book_description_and_back.setOnClickListener(new View.OnClickListener() { //nuh, i'd win
            public void onClick(View v) {
                String swicher = book_description_and_back.getText().toString();
                switch (swicher){
                    case "Описание":
                        book_description_and_back.setText("Читать");
                        userCursor = db.rawQuery("select Description from books where BookId=" + BookId, null);
                        userCursor.moveToFirst();
                        booktext.setText(userCursor.getString(0));
                        break;
                    case "Читать":
                        book_description_and_back.setText("Описание");
                        userCursor = db.rawQuery("select Text from books where BookId=" + BookId, null);
                        userCursor.moveToFirst();
                        booktext.setText(userCursor.getString(0));
                        break;
                }
            }
        });

        Button buttonFav = findViewById(R.id.button2);
        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBookFavoriteStatus();
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void checkBookFavoriteStatus() {


        String url = checkApiUrl;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        boolean isFavorite = false;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject favoriteBook = response.getJSONObject(i);
                                if (favoriteBook.getString("Email").equals(userEmail) &&
                                        favoriteBook.getInt("BookId") == BookId) {
                                    isFavorite = true;
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (isFavorite) {
                            removeBookFromFavorites();
                        } else {
                            addBookToFavorites();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                //textViewResponse.setText("Error occurred while checking favorites.");
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void addBookToFavorites() {
        String url = addApiUrl;

        JSONObject postData = new JSONObject();
        try {
            postData.put("Email", userEmail);
            postData.put("BookId", BookId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            // Обновление интерфейса или другие действия по завершению операции
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(getApplicationContext(), "Error occurred while adding to favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Добавление запроса в очередь запросов
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    private void removeBookFromFavorites() {
        String url = removeApiUrl;

        JSONObject postData = new JSONObject();
        try {
            postData.put("Email", userEmail);
            postData.put("BookId", BookId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            // Обновление интерфейса или другие действия по завершению операции
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(getApplicationContext(), "Error occurred while removing from favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Добавление запроса в очередь запросов
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}
