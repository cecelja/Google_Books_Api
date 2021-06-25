package com.example.googlebooksapi;

import android.graphics.Bitmap;

public class BookBitmap {

    //The title of the given book.
    private String mTitle;

    //The author of the given book.
    private String mAuthor;

    //The image resource for the cover of the book.
    private Bitmap mBitmap;

    public BookBitmap(Book book, Bitmap bitmap ){
        mAuthor = book.getmAuthor();
        mTitle = book.getmTitle();
        mBitmap = bitmap;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }
}
