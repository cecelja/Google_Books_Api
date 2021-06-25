package com.example.googlebooksapi;

public class Book {

    //The title of the given book.
    private String mTitle;

    //The author of the given book.
    private String mAuthor;

    //The image resource for the cover of the book.
    private String mImg;

    /*
    *   Create a new Book object
    *
    * @param title is the title of the book (eg. "Hamlet")
    * @param author is the author of the book (eg. "Charles Dickens")
    * @param img is the img of the cover of the book
    *
    * */
    public Book(String title, String author, String img){
        mTitle = title;
        mAuthor = author;
        mImg = img;
    }

    //With this method we get the title of the book.
    public String getmTitle() {
        return mTitle;
    }

    //With this method we get the author of the book.
    public String getmAuthor() {
        return mAuthor;
    }

    //With this method we get the image of the book.
    public String getmImg() {
        return mImg;
    }

}
