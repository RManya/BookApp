package com.example.android.books;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by admin on 7/3/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {
    public BookAdapter(Activity context, int resource) {
        super(context, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ListItemView = convertView;
        if (ListItemView == null) {
            ListItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book book = getItem(position);
        TextView titleTextView = (TextView) ListItemView.findViewById(R.id.title);
        titleTextView.setText(book.getTitle());

        TextView authorTextView = (TextView) ListItemView.findViewById(R.id.author);
        authorTextView.setText(book.getAuthor());

        return ListItemView;
    }
}
