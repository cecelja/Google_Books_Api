package com.example.googlebooksapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
public class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {
    private static final String LOG_TAG = BookLoader.class.getName();

    public String urls = null;
    private ArrayList<Bitmap> mapps;
    private boolean badUrl = false;
    private ArrayList<BookBitmap> bokic;

    public BookLoader (Context context, String url){
        super(context);
        urls = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Book> loadInBackground() {
        //Dont perform a network request if there are no urls
        if (urls == null){
            return null;
        }
        ArrayList<Book> books = Utils.fetchBookData(urls);
        for(int i = 0; i < books.size(); i++){
        new DownloadImage().execute(books.get(i).getmImg());
        }
        
        return books;
    }
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageURLString = strings[0];

            URL imageURL = Utils.createURL(imageURLString);
            Log.i(LOG_TAG, "This is imageURL " + imageURL);

            if(imageURL == null || imageURL.equals("")){
                badUrl = true;
                Log.i(LOG_TAG, "No it does not hit");
                return null;
            }
            Log.i(LOG_TAG, "Does it hit it?");
            Bitmap imageBitmap = null;
            HttpURLConnection imageConnection = null;
            InputStream imageStream = null;
            try {
                Log.i(LOG_TAG, "Connection start");
                imageConnection = (HttpURLConnection) imageURL.openConnection();
                Log.i(LOG_TAG, "Connection resume");
                imageConnection.connect();
                Log.i(LOG_TAG, "Failed connection");
                Log.i(LOG_TAG, "Response code " + imageConnection.getResponseCode());
                imageStream = imageConnection.getInputStream();
                Log.i(LOG_TAG, "This is bitmap stream " + imageStream);
                imageBitmap = BitmapFactory.decodeStream(imageStream);
            } catch (IOException e){
                Log.e(LOG_TAG, "Problems with the connection", e);
            }
            Log.i(LOG_TAG, "This is the image bitmap " + imageBitmap);
            return imageBitmap;

        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mapps.add(bitmap);
            Log.i(LOG_TAG, "This is bitmap ");
        }
}
