package com.example.android.books;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = QueryUtils.class.getSimpleName();

    BookAdapter mAdapter;
    EditText typed_text;
    TextView empty_list;
    ListView bookslistview;
    Button searchButton;
    static final String SEARCH_RESULTS = "Books Search Results";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        typed_text = (EditText) findViewById(R.id.edit_text);
         empty_list = (TextView) findViewById(R.id.listview_books_empty);

        bookslistview = (ListView) findViewById(R.id.list_view);
        mAdapter = new BookAdapter(this, -1);
        bookslistview.setAdapter(mAdapter);

        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internet_available()) {
                    BookAsynTask task = new BookAsynTask();
                    task.execute();
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
        if (savedInstanceState != null) {
            Book[] books = (Book[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            mAdapter.addAll(books);
        }

    }

    private boolean internet_available() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.isConnectedOrConnecting();
    }

    private String getUrl() {
        final String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=";
        String typedtext = typed_text.getText().toString();
        String final_text = typedtext.trim().replaceAll("\\s+","+");
        String url = baseUrl + final_text;
        Log.i("MainActivity",url);
        return url;
    }

    private void updateUI(List<Book> books) {
        if (books.isEmpty())
            empty_list.setVisibility(View.VISIBLE);
        else
            empty_list.setVisibility(View.GONE);
        mAdapter.clear();
        mAdapter.addAll(books);
    }

    private class BookAsynTask extends AsyncTask<URL, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(URL... urls) {
            URL url = createUrl(getUrl());
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Book> books = parseJson(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (books == null)
                return;

            updateUI(books);
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Problem building the URL ", e);
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = null;
            if (url == null)
                return jsonResponse;
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<Book> parseJson(String json) {
            if (json == null)
                return null;
            List<Book> books = QueryUtils.fetchbook(json);
            return books;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Book[] books = new Book[mAdapter.getCount()];
        for (int i = 0; i < books.length; i++) {
            books[i] = mAdapter.getItem(i);
        }
        outState.putParcelableArray(SEARCH_RESULTS, (Parcelable[]) books);
    }
}

