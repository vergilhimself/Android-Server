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

public class FetchBooksDataTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = FetchBooksDataTask.class.getSimpleName();
    private static final String API_URL = "http://192.168.0.104/api/books_api.php"; // Замените на ваш URL API
    private DatabaseHelper dbHelper;
    private MainActivity mainActivity;
    public FetchBooksDataTask(DatabaseHelper dbHelper, MainActivity mainActivity) {
        this.dbHelper = dbHelper;
        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {

            dbHelper.deleteAllBooks();

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
            Log.d(TAG, responseJson);

            // Обработка JSON-ответа
            JSONArray jsonArray = new JSONArray(responseJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int bookId = jsonObject.getInt("BookId");
                String title = jsonObject.getString("Title");
                String imageUrl = jsonObject.getString("ImageUrl");
                String author = jsonObject.getString("Author");
                int year;
                try {
                    year = Integer.parseInt(jsonObject.getString("Year"));
                } catch (NumberFormatException e) {
                    year = 0; // или любое другое значение по умолчанию
                }
                String genre = jsonObject.getString("Genre");
                String description = jsonObject.getString("Description");
                String text = jsonObject.getString("Text");

                // Добавление данных в локальную базу данных SQLite
                boolean isSuccess = dbHelper.addBook(title, imageUrl, author, year, genre, description, text);
                if (isSuccess) {
                    Log.d(TAG, "Book added successfully: " + title);
                } else {
                    Log.e(TAG, "Failed to add book: " + title);
                }
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error fetching data from API: " + e.getMessage());
        }
        return null;
    }
    // onPostExecute() вызывается после завершения doInBackground()
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

}
