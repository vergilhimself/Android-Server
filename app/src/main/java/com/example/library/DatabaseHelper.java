package com.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "library.db";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_BOOKS = "books";
    private static final String TABLE_READING_HISTORIES = "readinghistories";
    private static final String TABLE_FAVORITE_BOOKS = "favoritebooks";

    // Поля таблицы пользователей
    private static final String COL_USER_ID = "UserId";
    private static final String COL_EMAIL = "Email";
    private static final String COL_LOGIN = "Login";
    private static final String COL_PASSWORD = "Password";

    // Поля таблицы книг
    private static final String COL_BOOK_ID = "BookId";
    private static final String COL_TITLE = "Title";
    private static final String COL_IMAGE_URL = "ImageUrl";
    private static final String COL_AUTHOR = "Author";
    private static final String COL_YEAR = "Year";
    private static final String COL_GENRE = "Genre";
    private static final String COL_DESCRIPTION = "Description";
    private static final String COL_TEXT = "Text";

    // Поля таблицы истории чтения
    private static final String COL_READING_ID = "ReadingId";
    private static final String COL_LAST_READ_PAGE = "LastReadPage";

    // Поля таблицы любимых книг
    private static final String COL_FAVORITE_ID = "FavoriteId";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы пользователей
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_LOGIN + " TEXT, " +
                COL_PASSWORD + " TEXT)");

        // Создание таблицы книг
        db.execSQL("CREATE TABLE " + TABLE_BOOKS + " (" +
                COL_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_IMAGE_URL + " TEXT, " +
                COL_AUTHOR + " TEXT, " +
                COL_YEAR + " INTEGER, " +
                COL_GENRE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_TEXT + " TEXT)");

        // Создание таблицы истории чтения
        db.execSQL("CREATE TABLE " + TABLE_READING_HISTORIES + " (" +
                COL_READING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOK_ID + " INTEGER, " +
                COL_EMAIL + " TEXT, " +
                COL_LAST_READ_PAGE + " INTEGER, " +
                "FOREIGN KEY(" + COL_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COL_BOOK_ID + "), " +
                "FOREIGN KEY(" + COL_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COL_EMAIL + "))");

        // Создание таблицы любимых книг
        db.execSQL("CREATE TABLE " + TABLE_FAVORITE_BOOKS + " (" +
                COL_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOK_ID + " INTEGER, " +
                COL_EMAIL + " TEXT, " +
                "FOREIGN KEY(" + COL_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COL_BOOK_ID + "), " +
                "FOREIGN KEY(" + COL_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COL_EMAIL + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READING_HISTORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    // Добавление пользователя
    public boolean addUser(String email, String login, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_LOGIN, login);
        contentValues.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", null, null);
        db.close();
    }
    // Добавление книги
    public boolean addBook(String title, String imageUrl, String author, int year, String genre, String description, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Title", title);
        contentValues.put("ImageUrl", imageUrl);
        contentValues.put("Author", author);
        contentValues.put("Year", year);
        contentValues.put("Genre", genre);
        contentValues.put("Description", description);
        contentValues.put("Text", text);
        long result = db.insert("Books", null, contentValues);
        return result != -1;
    }
    // Метод для удаления всех записей из таблицы книг
    public void deleteAllBooks() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("Books", null, null);
        String resetQuery = "UPDATE sqlite_sequence SET seq = 1 WHERE name = ?";
        db.execSQL(resetQuery, new String[]{"books"});
        db.close();
    }

    // Добавление записи в историю чтения
    public boolean addReadingHistory(int bookId, String email, int lastReadPage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_BOOK_ID, bookId);
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_LAST_READ_PAGE, lastReadPage);
        long result = db.insert(TABLE_READING_HISTORIES, null, contentValues);
        return result != -1;
    }

    // Получение истории чтения по электронной почте пользователя
    public Cursor getReadingHistoryByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_READING_HISTORIES + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
    }

    // Добавление книги в список любимых
    public boolean addFavoriteBook(int bookId, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_BOOK_ID, bookId);
        contentValues.put(COL_EMAIL, email);
        long result = db.insert(TABLE_FAVORITE_BOOKS, null, contentValues);
        return result != -1;
    }

    public void deleteFavoriteBooks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favoritebooks", null, null);
        db.close();
    }
    // Получение списка любимых книг по электронной почте пользователя
    public Cursor getFavoriteBooksByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_FAVORITE_BOOKS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_USERS, null, null);
            db.delete(TABLE_BOOKS, null, null);
            db.delete(TABLE_READING_HISTORIES, null, null);
            db.delete(TABLE_FAVORITE_BOOKS, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
