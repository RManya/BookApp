package com.example.android.books;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 7/3/2017.
 */

public class Book implements Parcelable{
    private String mAuthor;
    private String mTitle;

    public Book(String title, String author) {
        mTitle = title;
        mAuthor = author;
    }

    public String getAuthor() {
        return mAuthor;
    }


    public String getTitle() {
        return mTitle;
    }

    protected Book(Parcel in) {
        mAuthor = in.readString();
        mTitle = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAuthor);
        parcel.writeString(mTitle);
    }
}
