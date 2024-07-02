package com.example.library;

import android.app.Dialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.*;
public class Book_content extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    String BookId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_book_content);

        Bundle arguments = getIntent().getExtras();
        BookId = arguments.get("id").toString();
        TextView booktext = findViewById(R.id.textView1);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        // создаем базу данных

        db = databaseHelper.getReadableDatabase();
        //получаем данные из бд в виде курсора
        userCursor = db.rawQuery("select Text from books where BookId=" + BookId, null);
        userCursor.moveToFirst();
        booktext.setText(userCursor.getString(0));

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
                UserManager userManager = UserManager.getInstance();
                String email = userManager.getEmail();
                userCursor = db.rawQuery("select * from favoritebooks where (Email= \" "+ email  +" \" and BookId="+BookId+")", null);
                if ((userCursor!= null) && (userCursor.getCount() > 0) ) {

                    String sqlq = "DELETE from favoritebooks where (Email= \" "+ email +"\" AND BookId="+BookId+")";
                    db.execSQL(sqlq);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Удалено из любимых!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    //db.execSQL("INSERT INTO favoriteBooks BookId, Email VALUES (" + BookId + ", 'sanya2012@gmail.com')");

                    db.execSQL("INSERT INTO \"main\".\"favoritebooks\"\n" +
                            "(\"BookId\", \"Email\")\n" +
                            "VALUES ("+BookId+", \""+email+"\");");
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Добавлено в любимые!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });


        /*book_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Book_content.this);
                dialog.setContentView(R.layout.book_description);

                // закрыть диалоговое окно при нажатии вне его, установите следующее:
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });*/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();

    }
}
