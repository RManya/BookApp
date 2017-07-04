package com.example.android.books;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 7/3/2017.
 */

public class QueryUtils {
    private QueryUtils() {
    }

    public static List<Book> fetchbook(String jsonString) {
        List<Book> books = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(jsonString);
            if (jsonResponse.getInt("totalItems") == 0)
                return books;
            JSONArray array = jsonResponse.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                JSONObject bookarray = array.getJSONObject(i);
                JSONObject bookInfo = bookarray.getJSONObject("volumeInfo");
                String title = bookInfo.getString("title");
                JSONArray authors = bookInfo.getJSONArray("authors");
                String authorsList = null;
                if (authors.length() == 0)
                    authorsList = null;
                else {
                    for (int j = 0; j < authors.length(); j++) {
                        if (i == 0) {
                            authorsList = authors.getString(0);
                        } else {
                            authorsList += ", " + authors.getString(i);
                        }
                    }
                }
                String author = authorsList;
                Book book = new Book(title, author);
                books.add(book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }
}
