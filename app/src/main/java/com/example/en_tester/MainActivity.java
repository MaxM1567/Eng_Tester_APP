package com.example.en_tester;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    // Объявим переменные компонентов
    TextView textView;

    // Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    // Словари с данными
    Map<String,String> word_translate = new HashMap<>(); // словарь: слово = перевод
    Map<String,Integer> word_error_rating = new HashMap<>(); // словарь: слово = кол-во ошибок

    // Список всех слов
    ArrayList<String> list_words = new ArrayList<>();

    // Обробатываемое слово
    String actual_word;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDBHelper = new DatabaseHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        Cursor cursor = mDb.rawQuery("SELECT * FROM words", null);
        cursor.moveToFirst();

        // Подключился к TextView
        textView = findViewById(R.id.textView5);

        // Генерация словарей
        while (!cursor.isAfterLast()) {
            // словарь: слово = перевод
            word_translate.put(cursor.getString(0), cursor.getString(1));

            // словарь: слово = кол-во ошибок
            word_error_rating.put(cursor.getString(0), cursor.getInt(2));
            cursor.moveToNext();
        }
        cursor.close();

        // Список ключей в словорях
        Set<String> keys = word_error_rating.keySet();

        // Генерация списка со словами в правильном соотношении
        for (String word : word_error_rating.keySet()) {
            for (int j = 0; j < word_error_rating.get(word); j++) {
                list_words.add(word);
            }
        }

        // Объявление обрабатываемого слова
        actual_word = getRandomElement(list_words);
        textView.setText(actual_word);
    }

    // Генератор случайного элемента списка
    public static<T> T getRandomElement(List<T> list) {
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
}
