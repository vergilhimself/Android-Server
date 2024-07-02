package com.example.library;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchUsersDataTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = FetchBooksDataTask.class.getSimpleName();
    private static final String API_URL = "http://192.168.0.104/api/users_api.php"; // Замените на ваш URL API
    private DatabaseHelper dbHelper;

    public FetchUsersDataTask(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {


            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // Получение ответа от API
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            inputStream.close();
            String responseJson = stringBuilder.toString();
            System.out.println(responseJson);
            // Обработка JSON-ответа
            JSONArray jsonArray = new JSONArray(responseJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String email = jsonObject.getString("Email");
                String login = jsonObject.getString("Login");
                String password = jsonObject.getString("Password");

                // Добавление данных в локальную базу данных SQLite
                boolean isSuccess = dbHelper.addUser(email, login, password);
                if (isSuccess) {
                    Log.d(TAG, "User added successfully: " + email);
                } else {
                    Log.e(TAG, "Failed to add user: " + email);
                }
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error fetching data from API: " + e.getMessage());
        }
        return null;
    }
}
